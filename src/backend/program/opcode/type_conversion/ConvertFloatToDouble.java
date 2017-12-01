package backend.program.opcode.type_conversion;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;

public class ConvertFloatToDouble extends Opcode {

	public ConvertFloatToDouble() {
		super("cvt.d.s");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.getFPRegFile().writeDouble(insn.getFPR1(), 
				new Float(Float.intBitsToFloat(prog.getFPRegFile().read(
						insn.getFPR2()).getValue())).doubleValue());
	}

}