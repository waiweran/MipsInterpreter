package backend.program.opcode.pseudoinstruction;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;

public class BranchNotEqualZero extends Opcode {

	public BranchNotEqualZero() {
		super("bnez");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		if(prog.getRegFile().read(insn.getR1()).getValue() != 0) {
			prog.jump(insn.getLabel());
		}
	}

}