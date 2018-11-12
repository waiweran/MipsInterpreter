package backend.state;
import java.util.HashMap;
import java.util.Map;

import backend.debugger.CallingConventionChecker;
import backend.program.Register;

/**
 * Stores the values held in the data registers.
 * @author Nathaniel
 * @version 11-05-2017
 */
public class RegisterFile {
	
	private Map<Register, Data> vals;
	private Data lo, hi;
	private CallingConventionChecker call;
	
	/**
	 * Initializes the register file.
	 * Preloads all registers with the value 0.
	 */
	public RegisterFile() {
		vals = new HashMap<>();
		lo = new Data();
		hi = new Data();
		for(Register r : Register.values()) {
			vals.put(r, new Data());
		}
		vals.put(Register.zero, new Data(0, Data.DataType.Integer));
		vals.put(Register.sp, new Data(Integer.MAX_VALUE + 1, Data.DataType.Address));
		vals.put(Register.gp, new Data(32768, Data.DataType.Address));
	}
	
	/**
	 * Writes the given value to the specified register.
	 * @param write to this register.
	 * @param value write this value.
	 */
	public void write(Register write, Data value) {
		if(!write.equals(Register.zero)) {
			vals.put(write, value);
		}
		if(call != null) call.writeReg(write);
	}
	
	/**
	 * Reads the value from the specified register.
	 * @param read this register.
	 * @return the value in this register.
	 */
	public Data read(Register read) {
		if(call != null) call.readReg(read);
		return vals.get(read);
	}
	
	/**
	 * Writes the given value to the LO register.
	 * @param value
	 */
	public void writeLO(Data value) {
		lo = value;
	}
	
	/**
	 * @return the value of the LO register.
	 */
	public Data readLO() {
		return lo;
	}	
	
	/**
	 * Writes the given value to the HI register.
	 * @param value
	 */
	public void writeHI(Data value) {
		hi = value;
	}
	
	/**
	 * @return the value of the HI register.
	 */
	public Data readHI() {
		return hi;
	}
	
	/**
	 * Sets the calling conventions checker.
	 * Initializes process of checking calling conventions.
	 * @param checker
	 */
	public void checkCallingConventions(CallingConventionChecker checker) {
		call = checker;
	}

}
