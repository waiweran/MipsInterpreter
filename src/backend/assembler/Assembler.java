package backend.assembler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import backend.program.Instruction;
import backend.program.Line;
import backend.program.Program;
import exceptions.InstructionFormatException;
import exceptions.UnsupportedOpcodeException;

/**
 * Assembles MIPS Code into binary.
 * @author Nathaniel
 * @version 03-18-2018
 */
public class Assembler {

	private static final ResourceBundle ASSEMBLE_FORMATS = 
			ResourceBundle.getBundle("backend.assembler.InsnFormats");
	private static final ResourceBundle PSEUDO_COMPONENTS = 
			ResourceBundle.getBundle("backend.assembler.Pseudoinsns");

	private Program prog;
	private Map<Integer, Integer> targets;

	/**
	 * Initializes the program assembler.
	 * @param program the program to initialize with.
	 */
	public Assembler(Program program) {
		prog = program;
		targets = new HashMap<>();
		mapTargets();
	}

	/**
	 * Assembles the program, writing the binary code to the given file.
	 * @param file The file to write the code to.
	 * @throws IOException if the file could not be opened.
	 * @throws InstructionFormatException if an instruction in the file is improperly formatted.
	 */
	public void assembleProgram(File file) throws IOException, InstructionFormatException {
		FileWriter fw = new FileWriter(file);
		PrintWriter pw = new PrintWriter(fw);
		int insnNum = 0;
		for(Line l : prog.getProgramLines()) {
			if(l.isExecutable()) {
				pw.println(assemble(l.getInstruction(), insnNum));
				insnNum++;
			}
		}
		pw.flush();
		fw.flush();
		pw.close();
		fw.close();
	}

	/**
	 * Assembles a single instruction.
	 * @param insn the instruction to assemble.
	 * @param insnNum The instruction number in the program (for relative branches).
	 * @return The binary string of the assembled instruction.
	 * @throws InstructionFormatException if instruction improperly formatted.
	 */
	public String assemble(Instruction insn, int insnNum) throws InstructionFormatException {
		if(ASSEMBLE_FORMATS.containsKey(insn.getOpcode().getName())) {
			String formatRef = ASSEMBLE_FORMATS.getString(insn.getOpcode().getName());
			return processInstruction(insn, insnNum, formatRef);
		}
		else if(PSEUDO_COMPONENTS.containsKey(insn.getOpcode().getName())) {
			String components = PSEUDO_COMPONENTS.getString(insn.getOpcode().getName());
			return processPseudoinstruction(insn, insnNum, components);
		}
		else {
			throw new UnsupportedOpcodeException("Unrecognized opcode "
					+ insn.getOpcode().getName());
		}

	}
	
	/**
	 * Processes a pseudoinstruction and converts it to multiple binary instructions
	 * @param insn The instruction to process.
	 * @param insnNum The number of the instruction in the program (for relative branches).
	 * @param components The reference components string for the pseudoinstruction.
	 * @return The binary string representation of the instructions.
	 * @throws InstructionFormatException if the instruction is improperly formatted.
	 */
	private String processPseudoinstruction(Instruction insn, int insnNum, String formatRef)
			throws InstructionFormatException {
		return processInstruction(insn, insnNum, formatRef).replaceAll("; ", " ");
	}

	/**
	 * Processes a single instruction to convert it to binary.
	 * @param insn The instruction to process.
	 * @param insnNum The number of the instruction in the program (for relative branches).
	 * @param formatRef The reference format string for the instruction.
	 * @return The binary string representation of the instruction.
	 * @throws InstructionFormatException if the instruction is improperly formatted.
	 */
	private String processInstruction(Instruction insn, int insnNum, String formatRef)
			throws InstructionFormatException {
		StringBuilder output = new StringBuilder();
		String[] pieces = formatRef.split(" ");
		for(String s : pieces) {
			if(s.equals("reg1")) {
				output.append(makeBinaryValue(insn.getR1().getRegisterNumber(), 5, 0));
			}
			else if(s.equals("reg2")) {
				try {
					output.append(makeBinaryValue(insn.getR2().getRegisterNumber(), 5, 0));
				} catch(NullPointerException e) {
					if(insn.getOpcode().getName().equals("sw") ||
							insn.getOpcode().getName().equals("lw")) {
						output.append("00000");
					}
					else {
						output.append(makeBinaryValue(insn.getR1().getRegisterNumber(), 5, 0));
					}
				}
			}
			else if(s.equals("reg3")) {
				output.append(makeBinaryValue(insn.getR3().getRegisterNumber(), 5, 0));
			}
			else if(s.equals("fpr1")) {
				output.append(makeBinaryValue(insn.getFPR1().getRegisterNumber(), 5, 0));
			}
			else if(s.equals("fpr2")) {
				output.append(makeBinaryValue(insn.getFPR2().getRegisterNumber(), 5, 0));
			}
			else if(s.equals("fpr3")) {
				output.append(makeBinaryValue(insn.getFPR3().getRegisterNumber(), 5, 0));
			}
			else if(s.startsWith("immediate")) {
				int start = Integer.parseInt(s.substring(s.indexOf("[") + 1, s.indexOf(":")));
				int end = Integer.parseInt(s.substring(s.indexOf(":") + 1, s.indexOf("]")));
				output.append(makeBinaryValue(insn.getImmed(), start, end));
			}
			else if(s.startsWith("target_absolute")) {
				int start = Integer.parseInt(s.substring(s.indexOf("[") + 1, s.indexOf(":")));
				int end = Integer.parseInt(s.substring(s.indexOf(":") + 1, s.indexOf("]")));
				int targetPC = targets.get(prog.getInsnRefs().get(insn.getTarget()));
				output.append(makeBinaryValue(targetPC, start, end));
			}
			else if(s.startsWith("target_relative")) {
				int start = Integer.parseInt(s.substring(s.indexOf("[") + 1, s.indexOf(":")));
				int end = Integer.parseInt(s.substring(s.indexOf(":") + 1, s.indexOf("]")));
				int targetPC = targets.get(prog.getInsnRefs().get(insn.getTarget()));
				output.append(makeBinaryValue(targetPC - insnNum + 1, start, end));
			}
			else {
				output.append(s);
			}
		}
		return output.toString();
	}

	/**
	 * Maps jump targets to PCs for assembling immediates.
	 */
	private void mapTargets() {
		int insnNum = 0;
		int lineNum = 0;
		for(Line l : prog.getProgramLines()) {
			if(l.isExecutable()) {
				insnNum++;
			}
			if(prog.getInsnRefs().containsValue(lineNum)) {
				targets.put(lineNum, insnNum);
			}
			lineNum++;
		}
	}

	/**
	 * Converts a number into a fixed length binary string.
	 * @param val The number to convert.
	 * @param length The length of the output binary string.
	 * @return the converted binary value.
	 * @throws InstructionFormatException if value is too large to convert to binary
	 */
	private String makeBinaryValue(int val, int start, int end) throws InstructionFormatException {
		StringBuilder output = new StringBuilder();
		String binaryVal = Integer.toBinaryString(val);
		for(int i = 0; i < start - binaryVal.length(); i++) {
			output.append("0");
		}
		if(binaryVal.length() > start) {
			throw new InstructionFormatException("Immediate too large");
		}
		else {
			output.append(binaryVal);
		}
		
		return output.toString().substring(0, output.length() - end);
	}

}
