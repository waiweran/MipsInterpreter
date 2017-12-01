package backend.program.opcode.specially_added;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;

public class Move extends Opcode {

	public Move() {
		super("move");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.getRegFile().write(insn.getR1(), 
			prog.getRegFile().read(insn.getR2()));
	}

}