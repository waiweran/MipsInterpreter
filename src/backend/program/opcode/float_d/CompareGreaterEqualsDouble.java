package backend.program.opcode.float_d;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;

public class CompareGreaterEqualsDouble extends Opcode {

	public CompareGreaterEqualsDouble() {
		super("c.ge.d");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.getFPRegFile().writeCond(
				prog.getFPRegFile().readDouble(insn.getFPR1()) >=
				prog.getFPRegFile().readDouble(insn.getFPR2()));
	}

}