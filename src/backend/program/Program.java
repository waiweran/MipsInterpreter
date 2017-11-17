package backend.program;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import backend.state.FPRegisterFile;
import backend.state.Memory;
import backend.state.RegisterFile;

/**
 * Stores all lines in the MIPS Program.
 * Holds data structures like registers and memory required
 * for execution of the program. 
 * @author Nathaniel
 * @version 11-05-2017
 */
public class Program {
	
	private RegisterFile regs;
	private FPRegisterFile fpRegs;
	private Memory mem;
	private List<Line> lines;
	private Map<String, Line> insnRefs;
	private InputStream in;
	private PrintStream out;
	private int pc;
	private boolean done;
	
	/**
	 * Initializes the program.
	 * @param input stream to use for Syscall.
	 * @param output stream to use for Syscall.
	 */
	public Program(InputStream input, PrintStream output) {
		regs = new RegisterFile();
		fpRegs = new FPRegisterFile();
		mem = new Memory(regs);
		lines = new ArrayList<>();
		insnRefs = new HashMap<>();
		in = input;
		out = output;
		done = false;
	}
	
	/**
	 * @return the data register file.
	 */
	public RegisterFile getRegFile() {
		return regs;
	}
	
	/**
	 * @return the floating point register file.
	 */
	public FPRegisterFile getFPRegFile() {
		return fpRegs;
	}
	
	/**
	 * @return main memory.
	 */
	public Memory getMem() {
		return mem;
	}
	
	/**
	 * @return Map containing instruction target pairings for jumps.
	 */
	public Map<String, Line> getInsnRefs() {
		return insnRefs;
	}
	
	/**
	 * @return List containing all lines of the program, in order.
	 */
	public List<Line> getProgramLines() {
		return lines;
	}
		
	/**
	 * Executes a jump to a given instruction target String.
	 * @param reference String target indicating the line to jump to.
	 */
	public void jump(String reference) {
		pc = lines.indexOf(insnRefs.get(reference));
	}
	
	/**
	 * @return the current PC, 
	 * index of next line to execute in the list of lines.
	 */
	public int getPC() {
		return pc;
	}
	
	/**
	 * Sets the PC,
	 * index of next line to execute.
	 * Used for jump register instructions.
	 * @param newPC the new PC value.
	 */
	public void setPC(int newPC) {
		pc = newPC;
	}
	
	/**
	 * @return the next Line to execute.
	 */
	public Line getNextLine() {
		return lines.get(pc++);
	}
	
	/**
	 * @return true if input for Syscall available, false if not.
	 */
	public boolean inputAvailable() {
		try {
			return in.available() > 0;
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * @return Gets input string for Syscall
	 */
	public String getInput() {
		StringBuilder output = new StringBuilder();
		while(inputAvailable()) {
			try {
				char c = (char)in.read();
				output.append(c);
				if(c == '\n') break;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return output.toString();
	}
	
	/**
	 * @return the output PrintStream for Syscall.
	 */
	public PrintStream getOutput() {
		return out;
	}
	
	/**
	 * Sets the program execution to be done.
	 * Program will stop running.
	 */
	public void done() {
		done = true;
	}
	
	/**
	 * @return true if program execution is complete, false if it is not.
	 */
	public boolean isDone() {
		return done;
	}

}
