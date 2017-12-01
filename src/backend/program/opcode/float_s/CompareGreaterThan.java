package backend.program.opcode.float_s;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;

public class CompareGreaterThan extends Opcode {

	public CompareGreaterThan() {
		super("c.gt.s");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.getFPRegFile().writeCond(
				convertToFloat(prog.getFPRegFile().read(insn.getFPR1())) >
				convertToFloat(prog.getFPRegFile().read(insn.getFPR2())));
	}

}