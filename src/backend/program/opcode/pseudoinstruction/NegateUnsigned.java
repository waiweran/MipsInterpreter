package backend.program.opcode.pseudoinstruction;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;
import backend.state.Data;

public class NegateUnsigned extends Opcode {

	public NegateUnsigned() {
		super("negu");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.getRegFile().write(insn.getR1(), 
			new Data(-prog.getRegFile().read(insn.getR2()).getValue()));
	}

}