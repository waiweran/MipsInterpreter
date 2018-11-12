package backend.program.opcode.arithmetic;

import backend.program.Instruction;
import backend.program.Program;
import backend.program.opcode.Opcode;
import backend.state.Data;

public class CountLeadingOnes extends Opcode {

	public CountLeadingOnes(String name) {
		super(name);
	}

	@Override
	public void execute(Instruction insn, Program prog) {
		Data data = prog.getRegFile().read(insn.getR2() == null? 
				insn.getR1() : insn.getR2());		
		String s = Integer.toBinaryString(data.getValue());
		int numOnes = 0;
		while(s.charAt(s.length() - 1 - numOnes) == '1') numOnes++;		
		prog.getRegFile().write(insn.getR1(), new Data(numOnes));
	}

}