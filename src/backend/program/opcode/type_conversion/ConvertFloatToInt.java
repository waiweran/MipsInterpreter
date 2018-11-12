package backend.program.opcode.type_conversion;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;
import backend.state.Data;

public class ConvertFloatToInt extends Opcode {

	public ConvertFloatToInt(String name) {
		super(name);
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.getFPRegFile().write(insn.getFPR1(), new Data((int)(
				(float)prog.getFPRegFile().read(insn.getFPR2()).getValue())));
	}

}