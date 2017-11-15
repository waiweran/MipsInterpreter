package floating_point;

import backend.program.Instruction;
import backend.program.Opcode;
import backend.program.Program;
import backend.program.Register;

public class FPInstruction extends Instruction {
	
	private FPRegister f1, f2, f3;
	private FPOpcode op;
	
	public FPInstruction(FPOpcode opcode, FPRegister fpReg1, FPRegister fpReg2, 
			FPRegister fpReg3, Register reg1, int immediate, String target) {
		super(Opcode.NoOperation, reg1, null, null, immediate, target);
		op = opcode;
		f1 = fpReg1;
		f2 = fpReg2;
		f3 = fpReg3;
	}
	
	public void execute(Program program) {
		op.getAction().accept(this, program);
	}
	
	public FPOpcode getFPOpcode() {
		return op;
	}
	
	public FPRegister getFPR1() {
		return f1;
	}	
	
	public FPRegister getFPR2() {
		return f2;
	}	
	
	public FPRegister getFPR3() {
		return f3;
	}

}
