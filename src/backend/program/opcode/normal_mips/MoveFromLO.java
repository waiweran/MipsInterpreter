package backend.program.opcode.normal_mips;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;

public class MoveFromLO extends Opcode {

	public MoveFromLO() {
		super("mflo");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.getRegFile().write(insn.getR1(), 
				prog.getRegFile().readLO());
	}

}