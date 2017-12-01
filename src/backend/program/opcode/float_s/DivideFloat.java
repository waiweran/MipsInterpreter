package backend.program.opcode.float_s;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;

public class DivideFloat extends Opcode {

	public DivideFloat() {
		super("div.s");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.getFPRegFile().write(insn.getFPR1(), convertToData(
				convertToFloat(prog.getFPRegFile().read(insn.getFPR2())) / 
				convertToFloat(prog.getFPRegFile().read(insn.getFPR3()))));
	}

}