package backend.log;

import backend.program.Register;
import backend.state.Data;
import backend.state.RegisterFile;

/**
 * Logs a change to a register.
 * @author Nathaniel
 * @version 01-24-2019
 */
public class RegisterLogEntry extends LogEntry {
	
	private RegisterFile regfile;
	private Register reg;
	private Data oldValue;

	/**
	 * Initializes the log entry.
	 * @param regs The register file.
	 * @param register The register modified.
	 * @param old The old value that is overwritten.
	 */
	public RegisterLogEntry(RegisterFile regs, Register register, Data old) {
		regfile = regs;
		reg = register;
		oldValue = old;
	}

	@Override
	public void undo() {
		regfile.writeSafe(reg, oldValue);
	}
	
	@Override
	public String toString() {
		return "Register: " + reg + " " + oldValue;
	}


}
