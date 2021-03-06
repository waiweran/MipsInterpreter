package backend.program.opcode.float_branch;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;

public class CompareGreaterEquals extends Opcode {

	public CompareGreaterEquals(String name) {
		super(name);
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.getFPRegFile().writeCond(
				convertToFloat(prog.getFPRegFile().read(insn.getFPR1())) >=
				convertToFloat(prog.getFPRegFile().read(insn.getFPR2())));
	}

}