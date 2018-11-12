package backend.program.opcode.jumpbranch;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.Register;
import backend.program.opcode.Opcode;
import backend.state.Data;

public class JumpAndLink extends Opcode {

	public JumpAndLink(String name) {
		super(name);
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.getRegFile().write(Register.ra, new Data(prog.getPC(), Data.DataType.J_Target));
		prog.jump(insn.getLabel());
	}

}