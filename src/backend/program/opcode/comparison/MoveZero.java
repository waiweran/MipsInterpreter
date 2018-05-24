package backend.program.opcode.comparison;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;

public class MoveZero extends Opcode {

	public MoveZero() {
		super("movn");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		if(prog.getRegFile().read(insn.getR3()).getValue() != 0)
			prog.getRegFile().write(insn.getR1(), prog.getRegFile().read(insn.getR2()));
	}

}