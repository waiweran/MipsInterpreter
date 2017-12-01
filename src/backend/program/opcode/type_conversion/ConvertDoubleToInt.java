package backend.program.opcode.type_conversion;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;
import backend.state.Data;

public class ConvertDoubleToInt extends Opcode {

	public ConvertDoubleToInt() {
		super("cvt.w.d");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.getFPRegFile().write(insn.getFPR1(), new Data((int)
				prog.getFPRegFile().readDouble(insn.getFPR2())));
	}

}