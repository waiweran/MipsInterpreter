package backend.program.opcode.normal_mips;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;
import backend.state.Data;

public class OrImmediate extends Opcode {

	public OrImmediate() {
		super("ori");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.getRegFile().write(insn.getR1(), 
				new Data(prog.getRegFile().read(insn.getR2() == null? 
						insn.getR1() : insn.getR2()).getValue() | 
				insn.getImmed()));
	}

}