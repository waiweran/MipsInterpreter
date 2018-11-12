package backend.program.opcode.multdiv;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;

public class MoveToHI extends Opcode {

	public MoveToHI(String name) {
		super(name);
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.getRegFile().writeHI(prog.getRegFile().read(insn.getR1()));
	}

}