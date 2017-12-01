package backend.program.opcode.normal_mips;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;

public class NoOperation extends Opcode {

	public NoOperation() {
		super("noop");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		/* do nothing */
	}

}