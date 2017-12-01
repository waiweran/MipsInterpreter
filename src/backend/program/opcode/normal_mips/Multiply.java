package backend.program.opcode.normal_mips;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;
import backend.state.Data;

public class Multiply extends Opcode {

	public Multiply() {
		super("mult");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		long output = (long)prog.getRegFile().read(insn.getR1()).getValue() * 
				(long)prog.getRegFile().read(insn.getR2()).getValue();
		long outLO = Long.rotateLeft(output, 32) >>> 32;
		long outHI = output >>> 32;
		prog.getRegFile().writeLO(new Data((int) outLO));
		prog.getRegFile().writeHI(new Data((int) outHI));
	}

}