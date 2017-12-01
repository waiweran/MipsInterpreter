package backend.program.opcode.normal_mips;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;

public class JumpRegister extends Opcode {

	public JumpRegister() {
		super("jr");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.setPC(prog.getRegFile().read(insn.getR1()).getValue());
	}

}