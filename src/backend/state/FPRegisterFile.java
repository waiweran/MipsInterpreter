package backend.state;
import java.util.HashMap;
import java.util.Map;

import backend.program.FPRegister;
import backend.state.Data.DataType;

/**
 * Stores values held in the floating point registers.
 * @author Nathaniel
 * @version 11-14-2017
 */
public class FPRegisterFile {
	
	private Map<FPRegister, Data> vals;
	private boolean cond;
	
	/**
	 * Initializes the register file.
	 */
	public FPRegisterFile() {
		vals = new HashMap<>();
		for(FPRegister r : FPRegister.values()) {
			vals.put(r, new Data());
		}
	}
	
	/**
	 * Writes the given value to the specified register.
	 * @param write to this register.
	 * @param value write this value.
	 */
	public void write(FPRegister write, Data value) {
		vals.put(write, value);
	}
	
	/**
	 * Reads the value from the specified register.
	 * @param read this register.
	 * @return the value in this register.
	 */
	public Data read(FPRegister read) {
		return vals.get(read);
	}
	
	/**
	 * Writes the given double value to the specified register and its
	 * partner for storing doubles.
	 * @param write the register to write to.
	 * Only even valued registers can be written to like this.
	 * @param value the Double to write to the registers.
	 */
	public void writeDouble(FPRegister write, double value) {
		long bits = Double.doubleToLongBits(value);
		int upper = (int)(bits >>> 32);
		int lower = (int)(Long.rotateLeft(bits, 32) >>> 32);
		vals.put(write, new Data(lower, DataType.Double_L));
		vals.put(write.getDoubleUpper(), new Data(upper, DataType.Double_H));
	}
	
	/**
	 * Reads a double value from the specified register and its partner
	 * for storing doubles.
	 * @param read the register to read from.
	 * Only even valued registers can be written to like this.
	 * @return the double value stored.
	 */
	public double readDouble(FPRegister read) {
		long lower = vals.get(read).getValue();
		long upper = vals.get(read.getDoubleUpper()).getValue();
		return Double.longBitsToDouble(lower + (upper << 32));
	}
	
	/**
	 * Writes to the Cond bit for floating point conditionals.
	 * @param value boolean to write to the bit.
	 */
	public void writeCond(boolean value) {
		cond = value;
	}
	
	/**
	 * @return current value of the Cond bit.
	 * For floating point conditionals.
	 */
	public boolean readCond() {
		return cond;
	}

}
