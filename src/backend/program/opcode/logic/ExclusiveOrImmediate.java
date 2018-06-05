package backend.program.opcode.logic;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;
import backend.state.Data;

public class ExclusiveOrImmediate extends Opcode {

	public ExclusiveOrImmediate() {
		super("xori");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		Data data = prog.getRegFile().read(insn.getR2() == null? 
				insn.getR1() : insn.getR2());
		prog.getRegFile().write(insn.getR1(), 
				new Data(data.getValue() ^ 
				insn.getImmed(), data.getDataType()));
	}

}