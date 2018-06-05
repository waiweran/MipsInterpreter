package backend.program.opcode.pseudoinstruction;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;
import backend.state.Data;

public class LoadImmediateFloat extends Opcode {

	public LoadImmediateFloat() {
		super("li.s");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.getFPRegFile().write(insn.getFPR1(), 
				new Data(insn.getImmed(), Data.DataType.Float));
	}

}