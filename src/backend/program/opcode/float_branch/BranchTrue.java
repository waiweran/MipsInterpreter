package backend.program.opcode.float_branch;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;

public class BranchTrue extends Opcode {

	public BranchTrue() {
		super("bc1t");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		if(prog.getRegFile().read(insn.getR1()).getValue() == 0) {
			prog.jump(insn.getTarget());
		}
	}

}