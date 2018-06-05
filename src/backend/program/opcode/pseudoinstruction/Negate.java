package backend.program.opcode.pseudoinstruction;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;
import backend.state.Data;

public class Negate extends Opcode {

	public Negate() {
		super("neg");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.getRegFile().write(insn.getR1(), 
			new Data(-prog.getRegFile().read(insn.getR2()).getValue()));
	}

}