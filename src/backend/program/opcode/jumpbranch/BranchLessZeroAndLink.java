package backend.program.opcode.jumpbranch;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.Register;
import backend.program.opcode.Opcode;
import backend.state.Data;

public class BranchLessZeroAndLink extends Opcode {

	public BranchLessZeroAndLink(String name) {
		super(name);
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		if(prog.getRegFile().read(insn.getR1()).getValue() < 0) {
			prog.getRegFile().write(Register.ra, new Data(prog.getPC(), Data.DataType.J_Target));
			prog.jump(insn.getLabel());
		}
	}

}