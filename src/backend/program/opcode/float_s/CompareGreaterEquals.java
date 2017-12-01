package backend.program.opcode.float_s;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;

public class CompareGreaterEquals extends Opcode {

	public CompareGreaterEquals() {
		super("c.ge.s");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.getFPRegFile().writeCond(
				convertToFloat(prog.getFPRegFile().read(insn.getFPR1())) >=
				convertToFloat(prog.getFPRegFile().read(insn.getFPR2())));
	}

}