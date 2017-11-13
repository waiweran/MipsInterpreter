package backend.debugger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import backend.program.Program;
import backend.program.Register;
import backend.state.Data;
import backend.state.RegisterFile;

/**
 * Class used to check for proper usage of the Stack.
 * @author Nathaniel
 * @version 11-13-2017
 */
public class StackUsage {
	
	private List<Register> saved;
	private Map<Register, Data> values;

	/**
	 * Initializes the Stack usage checker.
	 */
	public StackUsage(Program program) {
		saved = new ArrayList<>();
		initializeSavedRegisters();
		values = new HashMap<>();
	}
	
	/**
	 * Saves state of critical registers to check procedure call
	 * did not modify them.
	 */
	public void startProcedureCallCheck(RegisterFile registers) {
		for(Register reg: saved) {
			values.put(reg, registers.read(reg));
		}
	}
	
	/**
	 * Checks that a procedure just called followed proper register
	 * saving and restoring conventions.
	 * @return true if registers properly restored, false if not.
	 */
	public boolean endProcedureCallCheck(RegisterFile registers) {
		for(Register reg : saved) {
			if(!values.get(reg).equals(registers.read(reg))) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Initializes a list of registers that, by convention, 
	 * are saved in procedure calls.
	 */
	private void initializeSavedRegisters() {
		saved.add(Register.s0);
		saved.add(Register.s1);
		saved.add(Register.s2);
		saved.add(Register.s3);
		saved.add(Register.s4);
		saved.add(Register.s5);
		saved.add(Register.s6);
		saved.add(Register.s7);
		saved.add(Register.ra);
		saved.add(Register.sp);
	}

}
