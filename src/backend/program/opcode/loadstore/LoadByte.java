package backend.program.opcode.loadstore;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;

public class LoadByte extends Opcode {

	public LoadByte() {
		super("lb");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.getRegFile().write(insn.getR1(), 
				prog.getMem().loadByte(insn.getImmed() + 
				((insn.getR2() == null)? 0 : 
				prog.getRegFile().read(insn.getR2()).getValue()), true));
	}

}