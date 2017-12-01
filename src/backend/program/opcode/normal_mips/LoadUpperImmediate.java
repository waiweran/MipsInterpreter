package backend.program.opcode.normal_mips;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;
import backend.state.Data;

public class LoadUpperImmediate extends Opcode {

	public LoadUpperImmediate() {
		super("lui");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.getRegFile().write(insn.getR1(), 
				new Data(insn.getImmed() << 16));
	}

}