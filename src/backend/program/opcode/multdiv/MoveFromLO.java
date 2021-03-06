package backend.program.opcode.multdiv;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;

public class MoveFromLO extends Opcode {

	public MoveFromLO(String name) {
		super(name);
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.getRegFile().write(insn.getR1(), 
				prog.getRegFile().readLO());
	}

}