package backend.program.opcode.loadstore;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;

public class StoreByte extends Opcode {

	public StoreByte() {
		super("sb");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.getMem().storeByte(prog.getRegFile().read(insn.getR1()), 
				insn.getImmed() + ((insn.getR2() == null)? 
				0 : prog.getRegFile().read(insn.getR2()).getValue()));
	}

}