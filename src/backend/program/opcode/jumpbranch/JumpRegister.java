package backend.program.opcode.jumpbranch;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;

public class JumpRegister extends Opcode {

	public JumpRegister(String name) {
		super(name);
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.setPC(prog.getRegFile().read(insn.getR1()).getValue());
	}

}