package backend.assembler;

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

	private static final ResourceBundle PSEUDO_FORMATS = 
			ResourceBundle.getBundle("backend.assembler.PseudoFormats");

	private Program prog;

	/**
	 * Initializes the program assembler.
	 * @param program the program to initialize with.
	 */
	public Assembler(Program program) {
		prog = program;
	}

	/**
	 * Assembles a single instruction.
	 * @param line the instruction to assemble.  Must be an executable line.
	 * @param insnNum The instruction number in the program (for relative branches).
	 * @return The hexadecimal string of the assembled instruction.
	 * @throws InstructionFormatException if instruction improperly formatted.
	 */
	public String assemble(Line line, int insnNum) throws InstructionFormatException {
		Instruction insn = line.getInstruction();
		if(ASSEMBLE_FORMATS.containsKey(insn.getOpcode().getName())) {
			String formatRef = ASSEMBLE_FORMATS.getString(insn.getOpcode().getName());
			return processInstruction(line, insnNum, formatRef);
		}
		else if(PSEUDO_FORMATS.containsKey(insn.getOpcode().getName())) {
			String formatRef = PSEUDO_FORMATS.getString(insn.getOpcode().getName());
			checkUsage(line, formatRef, insn.makeUsedList());
			return "COMPOUND";
		}
		else {
			throw new UnsupportedOpcodeException("Opcode " + insn.getOpcode().getName() 
					+ " not supported");
		}

	}

	/**
	 * Processes a single instruction to convert it to hexadecimal.
	 * @param insn The instruction to process.
	 * @param insnNum The number of the instruction in the program (for relative branches).
	 * @param formatRef The reference format string for the instruction.
	 * @return The hex string representation of the instruction.
	 * @throws InstructionFormatException if the instruction is improperly formatted.
	 */
	private String processInstruction(Line line, int insnNum, String formatRef)
			throws InstructionFormatException {
		Instruction insn = line.getInstruction();
		StringBuilder output = new StringBuilder();
		StringBuilder used = new StringBuilder();
		String[] pieces = formatRef.split(" ");
		try {
			for(String s : pieces) {
				if(s.equals("reg1")) {
					try {
						output.append(makeBinaryValue(insn.getR1().getRegisterNumber(), 5, 0));
						used.append("reg1 ");
					} catch(NullPointerException e) {
						if(insn.getOpcode().getName().equals("swc1") ||
								insn.getOpcode().getName().equals("sdc1") ||
								insn.getOpcode().getName().equals("s.d") ||
								insn.getOpcode().getName().equals("l.d") ||
								insn.getOpcode().getName().equals("s.s") ||
								insn.getOpcode().getName().equals("l.s") ||
								insn.getOpcode().getName().equals("lwc1") ||
								insn.getOpcode().getName().equals("ldc1")) {
							output.append("00000");
						}
					}
				}
				else if(s.equals("reg2")) {
					try {
						output.append(makeBinaryValue(insn.getR2().getRegisterNumber(), 5, 0));
						used.append("reg2 ");
					} catch(NullPointerException e) {
						if(insn.getOpcode().getName().equals("sw") ||
								insn.getOpcode().getName().equals("lw")) {
							output.append("00000");
						}
						else {
							output.append(makeBinaryValue(insn.getR1().getRegisterNumber(), 5, 0));
							used.append("reg1 ");
						}
					}
				}
				else if(s.equals("reg3")) {
					output.append(makeBinaryValue(insn.getR3().getRegisterNumber(), 5, 0));
					used.append("reg3 ");
				}
				else if(s.equals("fpr1")) {
					output.append(makeBinaryValue(insn.getFPR1().getRegisterNumber(), 5, 0));
					used.append("fpr1 ");
				}
				else if(s.equals("fpr2")) {
					output.append(makeBinaryValue(insn.getFPR2().getRegisterNumber(), 5, 0));
					used.append("fpr2 ");
				}
				else if(s.equals("fpr3")) {
					output.append(makeBinaryValue(insn.getFPR3().getRegisterNumber(), 5, 0));
					used.append("fpr3 ");
				}
				else if(s.startsWith("immediate")) {
					int start = Integer.parseInt(s.substring(s.indexOf("[") + 1, s.indexOf(":")));
					int end = Integer.parseInt(s.substring(s.indexOf(":") + 1, s.indexOf("]")));
					if(insn.getImmed() == null && insn.getLabel() != null 
							&& prog.getMem().isDataReference(insn.getLabel())) {
						output.append(makeBinaryValue(prog.getMem().getMemoryAddress(insn.getLabel()), start, end));
						used.append("label ");
					}
					else {
						output.append(makeBinaryValue(insn.getImmed(), start, end));
						used.append("immediate ");
					}
				}
				else if(s.startsWith("target_absolute")) {
					int start = Integer.parseInt(s.substring(s.indexOf("[") + 1, s.indexOf(":")));
					int end = Integer.parseInt(s.substring(s.indexOf(":") + 1, s.indexOf("]")));
					int targetPC = prog.getTargetPC(insn.getLabel());
					output.append(makeBinaryValue(targetPC/4, start, end));
					used.append("label ");
				}
				else if(s.startsWith("target_relative")) {
					int start = Integer.parseInt(s.substring(s.indexOf("[") + 1, s.indexOf(":")));
					int end = Integer.parseInt(s.substring(s.indexOf(":") + 1, s.indexOf("]")));
					int targetPC = prog.getTargetPC(insn.getLabel());
					output.append(makeBinaryValue(targetPC/4 - insnNum + 1, start, end));
					used.append("label ");
				}
				else {
					output.append(s);
				}
			}
		}
		catch(Exception e) {
			throw new InstructionFormatException("Instruction missing a value", e, line);
		}
		if(output.toString().equals("UNSUPPORTED")) return output.toString();
		checkUsage(line, insn.makeUsedList(), used.toString());
		return toHex(output.toString());
	}

	/**
	 * Converts a number into a fixed length binary string.
	 * @param val The number to convert.
	 * @param length The length of the output binary string.
	 * @return the converted binary value.
	 * @throws InstructionFormatException if value is too large to convert to binary
	 */
	private String makeBinaryValue(int val, int start, int end) throws InstructionFormatException {
		if(val >= Math.pow(2, start) || val < -Math.pow(2,  start)) {
			throw new RuntimeException("Immediate too large");
		}
		StringBuilder output = new StringBuilder();
		String binaryVal = Integer.toBinaryString(val);
		for(int i = 0; i < start - binaryVal.length(); i++) {
			output.append("0");
		}
		if(binaryVal.length() > start) {
			output.append(binaryVal.substring(binaryVal.length() - start));
		}
		else {
			output.append(binaryVal);
		}
		
		return output.toString().substring(0, output.length() - end);
	}
	
	/**
	 * Checks that all components were properly used.
	 * @param format The components in the instruction format string.
	 * @param read The components in the instruction from the user.
	 * @throws InstructionFormatException if the lists don't match up.
	 */
	private void checkUsage(Line line, String format, String read) throws InstructionFormatException {
		String[] components = read.split(" ");
		for(String c : components) {
			if(!format.contains(c)) {
				throw new InstructionFormatException("Opcode not expecting " + c, line);
			}
		}
	}
	
	private String toHex(String binary) {
		StringBuilder output = new StringBuilder();
		for(int i = 0; i <binary.length(); i += 4){
			output.append(Integer.toHexString(
					Integer.parseInt(binary.substring(i, i+4), 2)));
		}
		return output.toString().toUpperCase();
	}

}
