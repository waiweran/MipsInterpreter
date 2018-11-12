package backend.program.opcode.float_s;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;

public class AddFloat extends Opcode {

	public AddFloat(String name) {
		super(name);
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.getFPRegFile().write(insn.getFPR1(), convertToData(
				convertToFloat(prog.getFPRegFile().read(insn.getFPR2())) + 
				convertToFloat(prog.getFPRegFile().read(insn.getFPR3()))));
	}

}