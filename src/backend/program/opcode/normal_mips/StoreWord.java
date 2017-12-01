package backend.program.opcode.normal_mips;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;

public class StoreWord extends Opcode {

	public StoreWord() {
		super("sw");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.getMem().storeWord(prog.getRegFile().read(insn.getR1()), 
				insn.getImmed() + ((insn.getR2() == null)? 
				0 : prog.getRegFile().read(insn.getR2()).getValue()));
	}

}