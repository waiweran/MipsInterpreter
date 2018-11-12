package backend.program.opcode.arithmetic;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;
import backend.state.Data;

public class Multiply extends Opcode {

	public Multiply(String name) {
		super(name);
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.getRegFile().write(insn.getR1(), 
				new Data(prog.getRegFile().read(insn.getR2()).getValue() * 
				prog.getRegFile().read(insn.getR3()).getValue(), 
				prog.getRegFile().read(insn.getR2()).getDataType()));
	}

}