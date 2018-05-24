package backend.program.opcode.float_branch;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;

public class CompareLessThanDouble extends Opcode {

	public CompareLessThanDouble() {
		super("c.lt.d");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.getFPRegFile().writeCond(
				prog.getFPRegFile().readDouble(insn.getFPR1()) <
				prog.getFPRegFile().readDouble(insn.getFPR2()));
	}

}