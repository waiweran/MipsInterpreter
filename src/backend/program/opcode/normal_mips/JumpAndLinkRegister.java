package backend.program.opcode.normal_mips;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.Register;
import backend.program.opcode.Opcode;
import backend.state.Data;

public class JumpAndLinkRegister extends Opcode {

	public JumpAndLinkRegister() {
		super("jalr");
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		prog.getRegFile().write(Register.ra, new Data(prog.getPC(), Data.DataType.J_Target));
		prog.setPC(prog.getRegFile().read(insn.getR1()).getValue());
	}

}