package backend.program;

import backend.debugger.CallingConventionChecker;
import backend.program.opcode.Opcode;
import backend.program.opcode.jumpbranch.JumpRegister;
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
	private Integer immed;
	private String jump;
	private CallingConventionChecker call;
	
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
	 * @throws InstructionFormatException 
	 */
	public Instruction(Opcode opcode, Register reg1, Register reg2, 
			Register reg3, FPRegister fpReg1, FPRegister fpReg2, 
			FPRegister fpReg3, Integer immediate, String label) {
		op = opcode;
		r1 = reg1;
		r2 = reg2;
		r3 = reg3;
		f1 = fpReg1;
		f2 = fpReg2;
		f3 = fpReg3;
		immed = immediate;
		jump = label;
	}
	
	/**
	 * Executes the given instruction.
	 * @param program The Program for the instruction execution to modify.
	 */
	public void execute(Program program) {
		op.execute(this, program);
		if(call != null) {
			if(op.getClass().getName().endsWith("AndLink")) call.startProcedure();
			if(op instanceof JumpRegister) call.endProcedure();
		}
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
	public Integer getImmed() {
		return immed;
	}
	
	/**
	 * @return the jump target.
	 */
	public String getLabel() {
		return jump;
	}
	
	/**
	 * Sets the immediate for the instruction if labels were used.
	 * @param immediate the new immediate value.
	 */
	public void setImmediate(int immediate) {
		immed = immediate;
	}
	
	/**
	 * @return a list of all components used by the current instruction.
	 */
	public String makeUsedList() {
		StringBuilder output = new StringBuilder();
		if(r1 != null) output.append("reg1 ");
		if(r2 != null) output.append("reg2 ");
		if(r3 != null) output.append("reg3 ");
		if(f1 != null) output.append("fpr1 ");
		if(f2 != null) output.append("fpr2 ");
		if(f3 != null) output.append("fpr3 ");
		if(immed != null) output.append("immediate ");
		if(jump != null) output.append("label ");
		return output.toString().trim();
	}
	
	/**
	 * Sets the calling conventions checker.
	 * Initializes process of checking calling conventions.
	 * @param checker
	 */
	public void checkCallingConventions(CallingConventionChecker checker) {
		call = checker;
	}


}
