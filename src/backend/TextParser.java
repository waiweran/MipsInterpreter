package backend;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TextParser {
	
	private Program prog;
	
	public TextParser(File code, Program program) {
		prog = program;
		readFile(code);
	}
	
	public Program getProgram() {
		return prog;
	}
	
	private void readFile(File code) {
		readData(code);
		readInstructions(code);
		checkTargets();
	}

	private void readInstructions(File code) {
		try {
			Scanner in = new Scanner(code);
			boolean insnSect = false;
			while(in.hasNextLine()) {
				String lineText = in.nextLine();
				if(lineText.contains(".data")) {
					insnSect = false;
				}
				if(lineText.contains(".text")) {
					insnSect = true;
					continue;
				}
				if(insnSect) makeInstruction(lineText);
			}
			in.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException ("File Not Found", e);
		}
	}

	private void readData(File code) {
		try {
			Scanner in = new Scanner(code);
			boolean dataSect = false;
			while(in.hasNextLine()) {
				String lineText = in.nextLine();
				if(lineText.contains(".data")) {
					dataSect = true;
					continue;
				}
				if(lineText.contains(".text")) {
					dataSect = false;
				}
				if(dataSect) makeData(lineText);
			}
			in.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException ("File Not Found", e);
		}
	}
	
	private void checkTargets() {
		for(Line l : prog.getProgramLines()) {
			if(l.isExecutable()) {
				String target = l.getInstruction().getTarget();
				if(!target.isEmpty() && !prog.getInsnRefs().containsKey(target)) {
					throw new RuntimeException("Improper Instruction Detected: " + l.toString());
				}
			}
		}
	}
	
	private void makeData(String line) {
		String text = line;
		if(line.indexOf('#') > 0) text = text.substring(0,  line.indexOf('#')); // Remove Comments
		text = text.trim(); // Remove leading and trailing whitespace
		if(text.startsWith(".align")) return; // Skip alignment commands
		if(text.isEmpty()) return; // Skip empty lines
		String[] dataSplit = text.split("\\s+", 3); // Split around remaining whitespace
		if(dataSplit.length < 3) throw new RuntimeException("Data entry too short: " + line);
		String reference = dataSplit[0];
		if(!reference.endsWith(":")) throw new RuntimeException("Improper Data Address Reference: " + reference);
		reference = reference.replaceAll(":", "");
		String dataType = dataSplit[1];
		String dataVal = dataSplit[2];
		
		if(dataType.equals(".space")) {
			int size = (int)Math.round(Math.ceil(Double.parseDouble(dataVal)/4.0));
			Data[] space = new Data[size];
			for(int i = 0; i < space.length; i++) {
				space[i] = new Data();
			}
			prog.getMem().addToGlobalData(reference, Arrays.asList(space));
		}
		else if(dataType.equals(".word")) {
			ArrayList<Data> output = new ArrayList<>();
			String[] dataVals = dataVal.split(",");
			for(int i = 0; i < dataVals.length; i++) {
				output.add(new Data(Integer.parseInt(dataVal.trim()), 
						Data.DataType.Integer, Data.Permissions.Read_Only));
			}
			prog.getMem().addToGlobalData(reference, output);
		}
		else if(dataType.equals(".asciiz")) {
			prog.getMem().addToGlobalData(reference, stringToDataArray(processString(dataVal)));
		}
		else if(dataType.equals(".halfword") || dataType.equals(".byte")) {
			throw new RuntimeException("Data types .halfword and .byte are not supported");
		}
		else {
			throw new RuntimeException("Improper Data Type: " + dataType);
		}
	}

	private void makeInstruction(String line) {
		String text = line;
		if(line.indexOf('#') >= 0) text = text.substring(0,  line.indexOf('#')); // Remove Comments
		text = text.replaceAll("[,()]", " "); // Remove unnecessary characters
		text = text.trim(); // Remove leading and trailing whitespace
		if(text.length() == 0) {
			prog.getProgramLines().add(new Line(line));
			return;
		}
		List<String> insnSplit = Arrays.asList(text.split("\\s+")); // Split around remaining whitespace
		String reference = "";
		String target = "";
		Opcode opcode = null;
		int regNum = 0;
		Register[] regs = new Register[3];
		int immed = 0;
		boolean immedUsed = false;
		for(String comp : insnSplit) {
			// If it's a line memory address reference
			if(comp.endsWith(":")) {
				if(reference.isEmpty()) {
					reference = comp.substring(0, comp.length() - 1);
				}
				else {
					throw new RuntimeException("Two references detected in one line");
				}
			}
			// If it's a register name
			else if(comp.startsWith("$")) {
				try {
					regs[regNum++] = Register.findRegister(comp);
				}
				catch(IndexOutOfBoundsException e) {
					throw new RuntimeException("Too many registers specified", e);
				}
			}
			// If it's an opcode
			else if(Opcode.isOpcode(comp)) {
				if(opcode != null) throw new RuntimeException("Two opcodes detected in one line");
				opcode = Opcode.findOpcode(comp);
			}
			// If it's a data memory address reference
			else if(prog.getMem().isDataReference(comp)) {
				immed = prog.getMem().getMemoryAddress(comp);
			}			
			else {
				// If it's an integer immediate
				try {
					immed = Integer.parseInt(comp);
					if(immedUsed) throw new RuntimeException("Only one immediate allowed");
					immedUsed = true;
				}
				catch(NumberFormatException e) {	
					// Only thing left is jump target
					if(target.isEmpty()) {
						target = comp;
					}
					else {
						throw new RuntimeException("Unrecognized Instruction Component: " + comp +
								" in line " + line);
					}
				}
			}	
		}
		if(opcode == null) {
			if(!reference.isEmpty() && regNum == 0 && !immedUsed) {
				Line madeLine = new Line(line);
				prog.getProgramLines().add(madeLine);
				prog.getInsnRefs().put(reference, madeLine);
			}
			else {
				throw new RuntimeException("No Opcode Found in Line: " + line);
			}
		}
		else {
			Line madeLine = new Line(line, new Instruction(opcode, regs[0], 
					regs[1], regs[2], immed, target));
			prog.getProgramLines().add(madeLine);
			if(!reference.isEmpty()) prog.getInsnRefs().put(reference, madeLine);
		}
	}
	
	private static String processString(String dataVal) {
		String inString = dataVal.substring(dataVal.indexOf(34) + 1, 
				dataVal.lastIndexOf(34));
		if(inString.contains("\\")) {
			inString = inString.replaceAll("\\\\n", "\n");
			inString = inString.replaceAll("\\\\r", "\r");
			inString = inString.replaceAll("\\\\t", "\t");
			inString = inString.replaceAll("\\\\\"", "\"");
			inString = inString.replaceAll("\\\\\\\\", "\\");
		}
		return inString;
	}
	
	public static List<Data> stringToDataArray(String dataVal) {
		ArrayList<Data> memOutput = new ArrayList<>();
		char[] charArray = (dataVal + "\0").toCharArray();		
		for(int i = 0; i < charArray.length; i += 4) {
			memOutput.add(new Data());
			for(int j = i; j < i + 4; j++) {
				if(j < charArray.length) {
					memOutput.set(i/4, new Data((memOutput.get(i/4).getValue() << 8)
							+ charArray[j], Data.DataType.String, Data.Permissions.Read_Only));
				}
				else {
					memOutput.set(i/4, new Data(memOutput.get(i/4).getValue() << 8, 
							Data.DataType.String, Data.Permissions.Read_Only));
				}
			}
		}
		return memOutput;
	}
	
	public static String dataArrayToString(List<Data> input) {
		StringBuilder output = new StringBuilder();
		for(int i = 0; i < input.size(); i++) {
			for(int j = 3; j >= 0; j--) {
				char nextChar = (char) ((input.get(i).getValue() >> 8*j) % 256);
				if(nextChar == (char)0) {
					return output.toString();
				}
				output.append(nextChar);
			}
		}
		throw new RuntimeException("Incomplete String: " + output.toString());
	}


}
