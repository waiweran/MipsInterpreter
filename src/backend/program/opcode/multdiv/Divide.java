package backend.program.opcode.multdiv;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;
import backend.state.Data;

public class Divide extends Opcode {

	public Divide(String name) {
		super(name);
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.getRegFile().writeLO(new Data(
				prog.getRegFile().read(insn.getR1()).getValue() / 
				prog.getRegFile().read(insn.getR2()).getValue(), 
				prog.getRegFile().read(insn.getR1()).getDataType()));
		prog.getRegFile().writeHI(new Data(
				prog.getRegFile().read(insn.getR1()).getValue() % 
				prog.getRegFile().read(insn.getR2()).getValue(), 
				prog.getRegFile().read(insn.getR1()).getDataType()));
	}

}