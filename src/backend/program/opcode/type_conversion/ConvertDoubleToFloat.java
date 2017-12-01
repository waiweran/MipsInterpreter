package backend.program.opcode.type_conversion;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;
import backend.state.Data;

public class ConvertDoubleToFloat extends Opcode {

	public ConvertDoubleToFloat() {
		super("cvt.s.d");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.getFPRegFile().write(insn.getFPR1(), new Data(Float.floatToIntBits(
				new Double(prog.getFPRegFile().readDouble(insn.getFPR2())).floatValue()), 
				Data.DataType.Float));
	}

}