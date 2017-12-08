package backend.program;

import backend.program.opcode.Opcode;
import exceptions.InstructionFormatException;

/**
 * Holds the information for a single MIPS Instruction.
 * @author Nathaniel
 * @version 11-14-2017
 */
public class Instruction {
	
	private Opcode op;
	private Register r1, r2, r3;
	private FPRegister f1, f2, f3;
	private int immed;
	private String jump;
	
	/**
	 * Constructs an instruction.
	 * @param opcode the instruction Opcode.
	 * @param reg1 First data register.
	 * @param reg2 Second data register.
	 * @param reg3 Third data register.
	 * @param fpReg1 First floating point register.
	 * @param fpReg2 Second floating point register.
	 * @param fpReg3 Third floating point register.
	 * @param immediate Instruction immediate.
	 * @param target Jump/Branch target.
	 */
	public Instruction(Opcode opcode, Register reg1, Register reg2, 
			Register reg3, FPRegister fpReg1, FPRegister fpReg2, 
			FPRegister fpReg3, int immediate, String target) {
		if(opcode == null) throw new InstructionFormatException("No Opcode Found");
		op = opcode;
		r1 = reg1;
		r2 = reg2;
		r3 = reg3;
		f1 = fpReg1;
		f2 = fpReg2;
		f3 = fpReg3;
		immed = immediate;
		jump = target;
	}
	
	/**
	 * Executes the given instruction.
	 * @param program The Program for the instruction execution to modify.
	 */
	public void execute(Program program) {
		op.execute(this, program);
	}
	
	/**
	 * @return the instruction's Opcode.
	 */
	public Opcode getOpcode() {
		return op;
	}
	
	/**
	 * @return the first data register argument.
	 */
	public Register getR1() {
		return r1;
	}	
	
	/**
	 * @return the second data register argument.
	 */
	public Register getR2() {
		return r2;
	}	
	
	/**
	 * @return the third data register argument.
	 */
	public Register getR3() {
		return r3;
	}
	
	/**
	 * @return the first floating point register argument.
	 */
	public FPRegister getFPR1() {
		return f1;
	}	
	
	/**
	 * @return the second floating point register argument.
	 */
	public FPRegister getFPR2() {
		return f2;
	}	
	
	/**
	 * @return the third floating point register argument.
	 */
	public FPRegister getFPR3() {
		return f3;
	}
	
	/**
	 * @return the immediate argument.
	 */
	public int getImmed() {
		return immed;
	}
	
	/**
	 * @return the jump target.
	 */
	public String getTarget() {
		return jump;
	}

}
