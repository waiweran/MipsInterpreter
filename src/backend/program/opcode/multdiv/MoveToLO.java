package backend.program.opcode.multdiv;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;

public class MoveToLO extends Opcode {

	public MoveToLO() {
		super("mtlo");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.getRegFile().writeLO(prog.getRegFile().read(insn.getR1()));
	}

}