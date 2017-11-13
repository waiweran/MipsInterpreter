package backend;

public class Instruction {
	
	private Opcode op;
	private Register r1, r2, r3;
	private int immed;
	private String jump;
	
	public Instruction(Opcode opcode, Register reg1, Register reg2, 
			Register reg3, int immediate, String target) {
		if(opcode == null) throw new RuntimeException("No Opcode Found");
		op = opcode;
		r1 = reg1;
		r2 = reg2;
		r3 = reg3;
		immed = immediate;
		jump = target;
	}
	
	public void execute(Program program) {
		op.getAction().accept(this, program);
	}
	
	public Opcode getOpcode() {
		return op;
	}
	
	public Register getR1() {
		return r1;
	}	
	
	public Register getR2() {
		return r2;
	}	
	
	public Register getR3() {
		return r3;
	}
	
	public int getImmed() {
		return immed;
	}
	
	public String getTarget() {
		return jump;
	}

}
