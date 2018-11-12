package backend.program.opcode.float_branch;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;

public class BranchFalse extends Opcode {

	public BranchFalse(String name) {
		super(name);
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		if(!prog.getFPRegFile().readCond()) {
			prog.jump(insn.getLabel());
		}
	}

}