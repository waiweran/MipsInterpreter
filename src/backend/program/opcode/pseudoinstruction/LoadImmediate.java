package backend.program.opcode.pseudoinstruction;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;
import backend.state.Data;

public class LoadImmediate extends Opcode {

	public LoadImmediate(String name) {
		super(name);
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.getRegFile().write(insn.getR1(), 
				new Data(insn.getImmed()));
	}

}