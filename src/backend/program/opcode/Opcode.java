package backend.program.opcode;

import backend.program.Instruction;
import backend.program.Program;
import backend.state.Data;
import exceptions.DataFormatException;
import exceptions.FPRegisterException;

/**
 * Abstract class defining a MIPS Opcode
 * Concrete subclasses define specific opcodes.
 * @author Nathaniel
 * @version 12-01-2017
 */
public abstract class Opcode {
	
	private String opName;
	
	/**
	 * Initializes the Opcode.
	 * @param name the Opcode name used in programs.
	 */
	protected Opcode(String name) {
		opName = name;
	}
	
	/**
	 * Executes the Opcode on the given instruction.
	 * @param insn Instruction containing the Opcode.
	 * @param prog Program in which the instruction is running.
	 */
	public abstract void execute(Instruction insn, Program prog);
	
	/**
	 * @return the Opcode name.
	 */
	public String getName() {
		return opName;
	}
		
	/**
	 * Converts a float to a Data object storing integer value.
	 * Uses IEEE 32 bit floating point format.
	 * @param value the float to convert.
	 * @return Data object representing the float. 
	 */
	protected Data convertToData(float value) {
		return new Data(Float.floatToIntBits(value), Data.DataType.Float);
	}
	
	/**
	 * Converts a Data value representing a float to the float.
	 * @param value Data storing an integer value representing a float
	 * in IEEE 32 bit floating point format.
	 * @return the float value.
	 * @throws DataFormatException if given data is not a float
	 */
	protected float convertToFloat(Data value) {
		if(!value.getDataType().equals(Data.DataType.Float))
			throw new FPRegisterException("Data Not a Float: "
					+ value.getDataType() + " " + value.toString());
		return Float.intBitsToFloat(value.getValue());
	}
	
}
