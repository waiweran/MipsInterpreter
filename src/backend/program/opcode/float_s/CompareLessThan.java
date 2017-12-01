package backend.program.opcode.float_s;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;

public class CompareLessThan extends Opcode {

	public CompareLessThan() {
		super("c.lt.s");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.getFPRegFile().writeCond(
				convertToFloat(prog.getFPRegFile().read(insn.getFPR1())) <
				convertToFloat(prog.getFPRegFile().read(insn.getFPR2())));
	}

}