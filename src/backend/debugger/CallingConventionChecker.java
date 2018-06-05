package backend.debugger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import backend.program.Register;
import backend.state.Data;
import backend.state.RegisterFile;

/**
 * Class used to check for proper usage of the Stack.
 * @author Nathaniel
 * @version 11-13-2017
 */
public class CallingConventionChecker {
	
	private RegisterFile registers;
	private int numViolations;
	private List<Register> saved, unsaved, unknown, arguments, returns;
	private Stack<Map<Register, Data>> values;

	/**
	 * Initializes the Stack usage checker.
	 */
	public CallingConventionChecker(RegisterFile regs) {
		registers = regs;
		numViolations = 0;
		initializeRegisterLists();
		values = new Stack<>();
		values.push(new HashMap<>());
		values.get(0).put(Register.ra, registers.read(Register.ra));
	}
	
	/**
	 * Saves state of critical registers to check procedure call
	 * did not modify them.
	 */
	public void startProcedure() {
		HashMap<Register, Data> regVals = new HashMap<>();
		for(Register reg: saved) {
			regVals.put(reg, registers.read(reg));
		}
		values.push(regVals);
		unknown.addAll(unsaved);
		unknown.addAll(returns);
	}
	
	/**
	 * Checks that a procedure just called followed proper calling conventions.
	 * @return true if saved registers properly restored, false if not.
	 */
	public boolean endProcedure() {
		boolean fine = true;
		Map<Register, Data> vals = values.pop();
		for(Register reg : saved) {
			if(vals.containsKey(reg) && !vals.get(reg).equals(registers.read(reg))) {
				numViolations++;
				fine = false;
			}
		}
		unknown.addAll(unsaved);
		unknown.addAll(arguments);
		return fine;
	}
	
	/**
	 * Notes that a register was written within the procedure.
	 * @param reg The register that was written.
	 */
	public void writeReg(Register reg) {
		unsaved.remove(reg);
	}
	
	/**
	 * Checks that the register that was read within the procedure was
	 * used with proper calling conventions.
	 * @param reg The register read.
	 * @return true if register does not have undetermined value 
	 * from procedure call, false if it does.
	 */
	public boolean readReg(Register reg) {
		if(unsaved.contains(reg)) {
			numViolations++;
			return false;
		}
		return true;
	}
	
	/**
	 * @return the total number of calling convention violations.
	 */
	public int getNumViolations() {
		return numViolations;
	}
	
	/**
	 * Initializes lists of registers.
	 */
	private void initializeRegisterLists() {
		unknown = new ArrayList<>();
		
		saved = new ArrayList<>();
		saved.add(Register.s0);
		saved.add(Register.s1);
		saved.add(Register.s2);
		saved.add(Register.s3);
		saved.add(Register.s4);
		saved.add(Register.s5);
		saved.add(Register.s6);
		saved.add(Register.s7);
		saved.add(Register.sp);
		saved.add(Register.fp);
		saved.add(Register.ra);
		
		unsaved = new ArrayList<>();
		unsaved.add(Register.t0);
		unsaved.add(Register.t1);
		unsaved.add(Register.t2);
		unsaved.add(Register.t3);
		unsaved.add(Register.t4);
		unsaved.add(Register.t5);
		unsaved.add(Register.t6);
		unsaved.add(Register.t7);
		unsaved.add(Register.t8);
		unsaved.add(Register.t9);
		
		arguments = new ArrayList<>();
		arguments.add(Register.a0);
		arguments.add(Register.a1);
		arguments.add(Register.a2);
		arguments.add(Register.a3);
		
		returns = new ArrayList<>();
		returns.add(Register.v0);
		returns.add(Register.v1);
	}

}
