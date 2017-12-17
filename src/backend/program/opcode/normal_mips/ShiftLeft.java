package backend.program.opcode.normal_mips;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;
import backend.state.Data;

public class ShiftLeft extends Opcode {

	public ShiftLeft() {
		super("sll");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		Data data = prog.getRegFile().read(insn.getR2() == null? 
				insn.getR1() : insn.getR2());
		prog.getRegFile().write(insn.getR1(), 
				new Data(data.getValue() << 
				insn.getImmed(), data.getDataType()));
	}

}