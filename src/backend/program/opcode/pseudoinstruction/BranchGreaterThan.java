package backend.program.opcode.pseudoinstruction;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;

public class BranchGreaterThan extends Opcode {

	public BranchGreaterThan() {
		super("bgt");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		if(prog.getRegFile().read(insn.getR1()).getValue()
				> prog.getRegFile().read(insn.getR2()).getValue()) {
			prog.jump(insn.getLabel());
		}
	}

}