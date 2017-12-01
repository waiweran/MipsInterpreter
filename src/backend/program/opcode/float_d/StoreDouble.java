package backend.program.opcode.float_d;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;

public class StoreDouble extends Opcode {

	public StoreDouble() {
		super("s.d");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.getMem().storeWord(prog.getFPRegFile().read(insn.getFPR1()), 
				insn.getImmed() + ((insn.getR1() == null)? 
				0 : prog.getRegFile().read(insn.getR1()).getValue()));
		prog.getMem().storeWord(prog.getFPRegFile().read(insn.getFPR1().getDoubleUpper()), 
				4 + insn.getImmed() + ((insn.getR1() == null)? 
				0 : prog.getRegFile().read(insn.getR1()).getValue()));
	}

}