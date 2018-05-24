package backend.program.opcode.loadstore;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;

public class LoadDouble extends Opcode {

	public LoadDouble() {
		super("ldc1");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.getFPRegFile().write(insn.getFPR1(), 
				prog.getMem().loadWord(insn.getImmed() + 
				((insn.getR1() == null)? 
				0 : prog.getRegFile().read(insn.getR1()).getValue())));
		prog.getFPRegFile().write(insn.getFPR1().getDoubleUpper(), 
				prog.getMem().loadWord(4 + insn.getImmed() + 
				((insn.getR1() == null)? 
				0 : prog.getRegFile().read(insn.getR1()).getValue())));
	}

}