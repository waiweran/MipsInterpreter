package backend.program.opcode.float_s;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;

public class LoadFloat extends Opcode {

	public LoadFloat() {
		super("l.s");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.getFPRegFile().write(insn.getFPR1(), 
				prog.getMem().loadWord(insn.getImmed() + 
				((insn.getR1() == null)? 
				0 : prog.getRegFile().read(insn.getR1()).getValue())));
	}

}