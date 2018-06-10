package backend.program.opcode.jumpbranch;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;

public class BranchEquals extends Opcode {

	public BranchEquals() {
		super("beq");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		if(prog.getRegFile().read(insn.getR1()).getValue() == 
				prog.getRegFile().read(insn.getR2()).getValue()) {
			prog.jump(insn.getLabel());
		}
	}

}