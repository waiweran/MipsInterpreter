package backend.program.opcode.pseudoinstruction;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;
import backend.state.Data;

public class LoadAddress extends Opcode {

	public LoadAddress() {
		super("la");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.getRegFile().write(insn.getR1(), 
				new Data(prog.getMem().getMemoryAddress(insn.getLabel()), 
						Data.DataType.Address));
	}

}