package backend.program.opcode.comparison;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;
import backend.state.Data;

public class SetLessThanImmediate extends Opcode {

	public SetLessThanImmediate(String name) {
		super(name);
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.getRegFile().write(insn.getR1(), 
				new Data((prog.getRegFile().read(insn.getR2()).getValue() < 
				insn.getImmed())? 1 : 0));
	}

}