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
	private List<Register> saved, specific, unsaved, unknown, arguments, returns;
	private Stack<Map<Register, Data>> values;
	private List<Integer> stackwrites;

	/**
	 * Initializes the Stack usage checker.
	 */
	public CallingConventionChecker(RegisterFile regs) {
		registers = regs;
		numViolations = 0;
		initializeRegisterLists();
		values = new Stack<>();
		values.push(new HashMap<>());
		values.get(0).put(Register.ra, registers.readSafe(Register.ra));
		stackwrites = new ArrayList<>();
	}
	
	/**
	 * Saves state of critical registers to check procedure call
	 * did not modify them.
	 */
	public void startProcedure() {
		stackwrites.clear();
		HashMap<Register, Data> regVals = new HashMap<>();
		for(Register reg: saved) {
			regVals.put(reg, registers.readSafe(reg));
		}
		for(Register reg: specific) {
			regVals.put(reg, registers.readSafe(reg));
		}
		values.push(regVals);
		unknown.clear();
		unknown.addAll(unsaved);
		unknown.addAll(returns);
	}
	
	/**
	 * Checks that a procedure just called followed proper calling conventions.
	 * @return true if saved registers properly restored, false if not.
	 */
	public boolean endProcedure() {
		stackwrites.clear();
		boolean fine = true;
		Map<Register, Data> vals = values.pop();
		for(Register reg : saved) {
			if(vals.containsKey(reg) && !vals.get(reg).equals(registers.readSafe(reg))) {
				numViolations++;
				fine = false;
			}
		}
		for(Register reg : specific) {
			if(vals.containsKey(reg) && !vals.get(reg).equals(registers.readSafe(reg))) {
				numViolations++;
				fine = false;
			}
		}
		unknown.clear();
		unknown.addAll(unsaved);
		unknown.addAll(arguments);
		return fine;
	}
	
	/**
	 * Notes that a register was written within the procedure.
	 * @param reg The register that was written.
	 */
	public void writeReg(Register reg) {
		unknown.remove(reg);
	}
	
	/**
	 * Checks that the register that was read within the procedure was
	 * used with proper calling conventions.
	 * @param reg The register read.
	 * @return true if register does not have undetermined value 
	 * from procedure call, false if it does.
	 */
	public boolean readReg(Register reg) {
		if(unknown.contains(reg)) {
			numViolations++;
			return false;
		}
		return true;
	}
	
	/**
	 * Notes that an address in the stack was written to.
	 * @param address.
	 */
	public void writeStack(int address) {
		int stackStart = registers.readSafe(Register.sp).getValue();
		if(stackStart > 0 && address > stackStart) {
			stackwrites.add(address);
		}
	}
	
	/**
	 * Checks to see if the program reads off the bottom of the stack.
	 * @param address.
	 * @return true if program does not read off bottom of stack, false if it does.
	 */
	public boolean readStack(int address) {
		int stackStart = registers.readSafe(Register.sp).getValue();
		if(stackStart > 0 && address > stackStart && !stackwrites.contains(address)) {
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
		
		specific = new ArrayList<>();
		specific.add(Register.sp);
		specific.add(Register.fp);
		specific.add(Register.ra);
		
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
