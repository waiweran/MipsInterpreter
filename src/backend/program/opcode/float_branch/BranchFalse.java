package backend.program.opcode.float_branch;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;

public class BranchFalse extends Opcode {

	public BranchFalse() {
		super("bc1f");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		if(prog.getRegFile().read(insn.getR1()).getValue() == 0) {
			prog.jump(insn.getTarget());
		}
	}

}