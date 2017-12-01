package backend.program.opcode;

import backend.program.Instruction;
import backend.program.Program;
import backend.state.Data;

public abstract class Opcode {
	
	private String opName;
	
	protected Opcode(String name) {
		opName = name;
	}
	
	public abstract void execute(Instruction insn, Program prog);
	
	public String getName() {
		return opName;
	}
		
	protected Data convertToData(float value) {
		return new Data(Float.floatToIntBits(value), Data.DataType.Float);
	}
	
	protected float convertToFloat(Data value) {
		if(!value.getDataType().equals(Data.DataType.Float))
			throw new RuntimeException("Data Not a Float: "
					+ value.getDataType() + " " + value.toString());
		return Float.intBitsToFloat(value.getValue());
	}
	
}
