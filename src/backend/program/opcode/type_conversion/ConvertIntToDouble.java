package backend.program.opcode.type_conversion;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;

public class ConvertIntToDouble extends Opcode {

	public ConvertIntToDouble(String name) {
		super(name);
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.getFPRegFile().writeDouble(insn.getFPR1(), 
				(double)prog.getFPRegFile().read(insn.getFPR2()).getValue());
	}

}