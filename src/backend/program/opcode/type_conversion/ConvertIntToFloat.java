package backend.program.opcode.type_conversion;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;
import backend.state.Data;

public class ConvertIntToFloat extends Opcode {

	public ConvertIntToFloat() {
		super("cvt.s.w");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.getFPRegFile().write(insn.getFPR1(), new Data(Float.floatToIntBits(
				(float)prog.getFPRegFile().read(insn.getFPR2()).getValue()), 
				Data.DataType.Float));
	}

}