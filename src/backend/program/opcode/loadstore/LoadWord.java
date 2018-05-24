package backend.program.opcode.loadstore;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;

public class LoadWord extends Opcode {

	public LoadWord() {
		super("lw");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.getRegFile().write(insn.getR1(), 
				prog.getMem().loadWord(insn.getImmed() + 
				((insn.getR2() == null)? 0 : 
				prog.getRegFile().read(insn.getR2()).getValue())));
	}

}