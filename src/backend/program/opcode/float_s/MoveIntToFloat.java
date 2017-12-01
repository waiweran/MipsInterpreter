package backend.program.opcode.float_s;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;

public class MoveIntToFloat extends Opcode {

	public MoveIntToFloat() {
		super("mtc1");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.getFPRegFile().write(insn.getFPR1(), 
				prog.getRegFile().read(insn.getR1()));
	}

}