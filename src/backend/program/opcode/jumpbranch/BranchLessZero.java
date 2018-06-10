package backend.program.opcode.jumpbranch;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;

public class BranchLessZero extends Opcode {

	public BranchLessZero() {
		super("bltz");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		if(prog.getRegFile().read(insn.getR1()).getValue() < 0) {
			prog.jump(insn.getLabel());
		}
	}

}