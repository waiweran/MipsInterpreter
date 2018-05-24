package backend.program.opcode.jumpbranch;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;

public class Jump extends Opcode {

	public Jump() {
		super("j");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.jump(insn.getTarget());
	}

}