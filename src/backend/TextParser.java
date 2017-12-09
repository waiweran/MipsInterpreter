package backend;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import backend.program.FPRegister;
import backend.program.Instruction;
import backend.program.Line;
import backend.program.Program;
import backend.program.Register;
import backend.program.opcode.Opcode;
import backend.program.opcode.OpcodeFactory;
import backend.state.Data;
import exceptions.DataFormatException;
import exceptions.ExecutionException;
import exceptions.InstructionFormatException;
import exceptions.MIPSException;
import exceptions.UnsupportedDataException;

/**
 * Parses a text file and converts it into MIPS Instructions.
 * Stores instructions in a Program data structure.
 * @author Nathaniel
 * @version 11-04-2017
 */
public class TextParser {
	
	private Program prog;
	
	/**
	 * Initializes the Text Parser.
	 * @param code the File to parse.
	 * @param program the Program to add the parsed instructions to.
	 * @throws FileNotFoundException 
	 * @throws MIPSException 
	 */
	public TextParser(File code, Program program) throws FileNotFoundException, MIPSException {
		prog = program;
		readFile(code);
	}
	
	/**
	 * @return the Program the instructions are added to.
	 */
	public Program getProgram() {
		return prog;
	}
	
	/**
	 * Reads the given file.
	 * @param code File to read.
	 * @throws FileNotFoundException 
	 * @throws DataFormatException 
	 * @throws InstructionFormatException 
	 * @throws MIPSException if processed instruction incorrectly formatted
	 */
	private void readFile(File code) throws FileNotFoundException, 
			DataFormatException, InstructionFormatException {
		readData(code);
		readInstructions(code);
		checkTargets();
	}

	/**
	 * Reads Instructions out of the given file.
	 * @param code File to read.
	 * @throws FileNotFoundException 
	 * @throws InstructionFormatException 
	 * @throws MIPSException if improperly formatted instruction found
	 */
	private void readInstructions(File code) throws FileNotFoundException, InstructionFormatException {
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
			if(insnSect) {
				makeInstruction(lineText);
			}
		}
		in.close();
	}

	/**
	 * Reads global data values out of the file.
	 * @param code File to read.
	 * @throws FileNotFoundException 
	 * @throws DataFormatException 
	 */
	private void readData(File code) throws FileNotFoundException, DataFormatException {
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
	}
	
	/**
	 * Checks that all stored instruction targets are valid.
	 * Used to check that all branches and jumps have a valid 
	 * target once the full program is parsed.
	 * @throws InstructionFormatException if invalid target found
	 */
	private void checkTargets() throws InstructionFormatException {
		for(Line l : prog.getProgramLines()) {
			if(l.isExecutable()) {
				String target = l.getInstruction().getTarget();
				if(!target.isEmpty() && !prog.getInsnRefs().containsKey(target)) {
					throw new InstructionFormatException("Improper Jump Reference Detected: " + l.toString());
				}
			}
		}
	}
	
	/**
	 * Converts a line in the file to a global data entry.
	 * @param line line of file to convert.
	 * @throws DataFormatException 
	 */
	private void makeData(String line) throws DataFormatException {
		String text = line;
		if(line.indexOf('#') > 0) text = text.substring(0,  line.indexOf('#')); // Remove Comments
		text = text.trim(); // Remove leading and trailing whitespace
		if(text.startsWith(".align")) return; // Skip alignment commands
		if(text.isEmpty()) return; // Skip empty lines
		String[] dataSplit = text.split("\\s+", 3); // Split around remaining whitespace
		if(dataSplit.length < 3) throw new DataFormatException("Data entry too short: " + line);
		String reference = dataSplit[0];
		if(!reference.endsWith(":")) throw new DataFormatException("Improper Data Address Reference: " + reference);
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
						Data.DataType.Integer));
			}
			prog.getMem().addToGlobalData(reference, output);
		}
		else if(dataType.equals(".float")) {
			ArrayList<Data> output = new ArrayList<>();
			String[] dataVals = dataVal.split(",");
			for(int i = 0; i < dataVals.length; i++) {
				output.add(new Data(Float.floatToIntBits(Float.parseFloat(dataVal.trim())), 
						Data.DataType.Float));
			}
			prog.getMem().addToGlobalData(reference, output);
		}
		else if(dataType.equals(".double")) {
			ArrayList<Data> output = new ArrayList<>();
			String[] dataVals = dataVal.split(",");
			for(int i = 0; i < dataVals.length; i++) {
				long doubleVal = Double.doubleToLongBits(Double.parseDouble(dataVal.trim()));
				int bottom = (int)(Long.rotateLeft(doubleVal, 32) >>> 32);
				int top = (int)(doubleVal >>> 32);
				output.add(new Data(bottom, Data.DataType.Double_L));
				output.add(new Data(top, Data.DataType.Double_H));
			}
			prog.getMem().addToGlobalData(reference, output);
		}
		else if(dataType.equals(".asciiz")) {
			prog.getMem().addToGlobalData(reference, stringToDataArray(processString(dataVal)));
		}
		else if(dataType.equals(".halfword") || dataType.equals(".byte")) {
			throw new UnsupportedDataException("Data types .halfword and .byte are not supported");
		}
		else {
			throw new DataFormatException("Improper Data Type: " + dataType);
		}
	}

	/**
	 * Converts a line of the file to an instruction.
	 * @param line the line to convert.
	 * @throws InstructionFormatException 
	 */
	private void makeInstruction(String line) throws InstructionFormatException {
		OpcodeFactory opFactory = new OpcodeFactory();
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
		int fpRegNum = 0;
		FPRegister[] fpRegs = new FPRegister[3];
		int immed = 0;
		boolean immedUsed = false;
		for(String comp : insnSplit) {
			// If it's a line memory address reference
			if(comp.endsWith(":")) {
				if(reference.isEmpty()) {
					reference = comp.substring(0, comp.length() - 1);
				}
				else {
					prog.getProgramLines().add(new Line(line));
					throw new InstructionFormatException("Two references detected in one line");
				}
			}
			// If it's a register name
			else if(comp.startsWith("$")) {
				try {
					regs[regNum] = Register.findRegister(comp);
					regNum++;
				}
				catch(IndexOutOfBoundsException e) {
					prog.getProgramLines().add(new Line(line));
					throw new InstructionFormatException("Too many registers specified", e);
				}
				catch(InstructionFormatException e1) {
					try {
						fpRegs[fpRegNum] = FPRegister.findRegister(comp);
						fpRegNum++;
					}
					catch(IndexOutOfBoundsException e2) {
						prog.getProgramLines().add(new Line(line));
						throw new InstructionFormatException("Too many registers specified", e2);
					}
				}
			}
			// If it's an opcode
			else if(opFactory.isOpcode(comp)) {
				if(opcode != null) {
					prog.getProgramLines().add(new Line(line));
					throw new InstructionFormatException("Two opcodes detected in one line");
				}
				opcode = opFactory.findOpcode(comp);
			}
			// If it's a data memory address reference
			else if(prog.getMem().isDataReference(comp)) {
				immed = prog.getMem().getMemoryAddress(comp);
			}			
			else {
				// If it's an integer immediate
				try {
					immed = Integer.parseInt(comp);
					if(immedUsed) {
						prog.getProgramLines().add(new Line(line));
						throw new InstructionFormatException("Only one immediate allowed");
					}
					immedUsed = true;
				}
				catch(NumberFormatException e) {	
					// Only thing left is jump target
					if(target.isEmpty()) {
						target = comp;
					}
					else {
						prog.getProgramLines().add(new Line(line));
						throw new InstructionFormatException("Unrecognized Instruction Component: " + comp);
					}
				}
			}	
		}
		if(opcode == null) {
			if(!reference.isEmpty() && regNum == 0  && fpRegNum == 0
					&& !immedUsed && target.isEmpty()) {
				Line madeLine = new Line(line);
				prog.getProgramLines().add(madeLine);
				prog.getInsnRefs().put(reference, madeLine);
			}
			else {
				prog.getProgramLines().add(new Line(line));
				throw new InstructionFormatException("No Opcode Found in Line: " + line);
			}
		}
		else {
			Line madeLine = new Line(line, new Instruction(opcode, regs[0], 
					regs[1], regs[2], fpRegs[0], fpRegs[1], fpRegs[2], immed, target));
			prog.getProgramLines().add(madeLine);
			if(!reference.isEmpty()) prog.getInsnRefs().put(reference, madeLine);
		}
	}
	
	/**
	 * Converts escape characters in a string to the actual escape character.
	 * @param dataVal the input string to process.
	 * @return processed string with proper escape character values.
	 */
	private String processString(String dataVal) {
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
	
	/**
	 * Converts a String to a list of Data.
	 * Splits string into chars, stores each char as a byte, 
	 * clumps 4 consecutive bytes into a word, and saves that
	 * as a Data value.
	 * @param dataVal the String to convert.
	 * @return List of Data values produced from the string.
	 */
	public static List<Data> stringToDataArray(String dataVal) {
		ArrayList<Data> memOutput = new ArrayList<>();
		char[] charArray = (dataVal + "\0").toCharArray();		
		for(int i = 0; i < charArray.length; i += 4) {
			memOutput.add(new Data());
			for(int j = i; j < i + 4; j++) {
				if(j < charArray.length) {
					memOutput.set(i/4, new Data((memOutput.get(i/4).getValue() << 8)
							+ charArray[j], Data.DataType.String));
				}
				else {
					memOutput.set(i/4, new Data(memOutput.get(i/4).getValue() << 8, 
							Data.DataType.String));
				}
			}
		}
		return memOutput;
	}
	
	/**
	 * Converts a list of Data values to a String.
	 * Takes each Data value, splits it into 4 bytes, converts
	 * each byte into a character, and concatenates the characters
	 * into the output string.
	 * @param input the list of Data values to convert to a String.
	 * @return output String produced from Data values.
	 */
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
		throw new ExecutionException("Incomplete String: " + output.toString());
	}

}
