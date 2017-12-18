package backend.debugger;

import java.util.ResourceBundle;

import backend.program.Instruction;
import backend.program.opcode.Opcode;
import exceptions.InstructionFormatException;

public class InsnFormatChecker {
	
	private static final ResourceBundle INSN_FORMATS = 
			ResourceBundle.getBundle("backend.debugger.OpcodeFormats");
	
	/**
	 * Checks the formatting of the given instruction.
	 * @param insn the instruction to check.
	 * @throws InstructionFormatException if improper formatting detected.
	 */
	public void checkFormat(Instruction insn) throws InstructionFormatException {
		checkArgs(insn);
		Opcode op = insn.getOpcode();
		String formatRef = INSN_FORMATS.getString(op.getName());
		if(!getFormat(insn).equals(formatRef))
			throw new InstructionFormatException("Improper instruction type: "
					+ getFormat(insn) + " " + formatRef + " " + insn.getOpcode().getName());
	}
	
	/**
	 * Checks for invalid combinations of arguments.
	 * @param insn the instruction to check the arguments of.
	 * @throws InstructionFormatException if invalid arguments found.
	 */
	private void checkArgs(Instruction insn) throws InstructionFormatException{
		int numIntArgs = 0;
		int numGenArgs = 0;
		int numFpArgs = 0;
		if(insn.getR1() != null) numIntArgs++;
		if(insn.getR2() != null) numIntArgs++;
		if(insn.getR3() != null) numIntArgs++;
		if(insn.getImmed() != null) numGenArgs++;
		if(!insn.getTarget().isEmpty()) numGenArgs++;		
		if(insn.getFPR1() != null) numFpArgs++;
		if(insn.getFPR2() != null) numFpArgs++;
		if(insn.getFPR3() != null) numFpArgs++;

		if(numIntArgs + numGenArgs + numFpArgs > 3) {
			throw new InstructionFormatException(
					"Too many arguments in this instruction");
		}
		if(numGenArgs > 1) {
			throw new InstructionFormatException(
					"Instruction contains 2 immediates.  Can only have 1 immediate");			
		}		
		if(numFpArgs != 0 && numIntArgs == 0 && numGenArgs != 0) {
			throw new InstructionFormatException(
					"FP Instructions, other than load/store, do not take immediates");					
		}
		if(numIntArgs > 1 && numFpArgs > 1) {
			throw new InstructionFormatException(
					"Unrecognized combination of integer and FP registers");
		}
	}
	
	/**
	 * Determines the instruction format from the given instruction.
	 * @param insn the instruction to get the format of.
	 * @return String describing instruction format (R, I, or J)
	 */
	private String getFormat(Instruction insn) {
		StringBuilder output = new StringBuilder();
		int numIntArgs = 0;
		int numGenArgs = 0;
		int numFpArgs = 0;
		if(insn.getR1() != null) numIntArgs++;
		if(insn.getR2() != null) numIntArgs++;
		if(insn.getR3() != null) numIntArgs++;
		if(insn.getImmed() != null) numGenArgs++;
		if(!insn.getTarget().isEmpty()) numGenArgs++;		
		if(insn.getFPR1() != null) numFpArgs++;
		if(insn.getFPR2() != null) numFpArgs++;
		if(insn.getFPR3() != null) numFpArgs++;
		
		if(numGenArgs > 0) {
			if(numIntArgs + numFpArgs > 0) output.append("I");
			else output.append("J");
		}
		else {
			if(numIntArgs == 0) output.append("J");
			else output.append("R");
		}
		if(numIntArgs > 0) output.append(".i");
		if(numFpArgs > 0) output.append(".f");
		return output.toString();
	}

}
