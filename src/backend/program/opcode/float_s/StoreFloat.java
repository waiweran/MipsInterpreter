package backend.program.opcode.float_s;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;

public class StoreFloat extends Opcode {

	public StoreFloat() {
		super("s.s");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.getMem().storeWord(prog.getFPRegFile().read(insn.getFPR1()), 
				insn.getImmed() + ((insn.getR1() == null)? 
				0 : prog.getRegFile().read(insn.getR1()).getValue()));
	}

}