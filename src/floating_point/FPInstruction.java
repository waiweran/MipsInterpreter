package floating_point;

import backend.program.Program;

public class FPInstruction {
	
	private FPOpcode op;
	private FPRegister f1, f2, f3;
	private int immed;
	private String jump;
	
	public FPInstruction(FPOpcode opcode, FPRegister reg1, FPRegister reg2, 
			FPRegister reg3, int immediate, String target) {
		if(opcode == null) throw new RuntimeException("No Opcode Found");
		op = opcode;
		f1 = reg1;
		f2 = reg2;
		f3 = reg3;
		immed = immediate;
		jump = target;
	}
	
	public void execute(Program program) {
		op.getAction().accept(this, program);
	}
	
	public FPOpcode getOpcode() {
		return op;
	}
	
	public FPRegister getR1() {
		return f1;
	}	
	
	public FPRegister getR2() {
		return f2;
	}	
	
	public FPRegister getR3() {
		return f3;
	}
	
	public int getImmed() {
		return immed;
	}
	
	public String getTarget() {
		return jump;
	}

}
