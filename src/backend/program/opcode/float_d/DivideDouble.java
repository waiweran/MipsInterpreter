package backend.program.opcode.float_d;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;

public class DivideDouble extends Opcode {

	public DivideDouble() {
		super("div.d");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.getFPRegFile().writeDouble(insn.getFPR1(),
				prog.getFPRegFile().readDouble(insn.getFPR2()) / 
				prog.getFPRegFile().readDouble(insn.getFPR3()));
	}

}