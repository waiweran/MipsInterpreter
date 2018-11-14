package backend.program;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import backend.debugger.CallingConventionChecker;
import backend.program.opcode.Syscall;
import backend.program.opcode.arithmetic.AddImmediate;
import backend.program.opcode.jumpbranch.Jump;
import backend.state.Data;
import backend.state.FPRegisterFile;
import backend.state.Memory;
import backend.state.RegisterFile;
import exceptions.LabelException;

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
	private Map<String, Integer> lineRefs;
	private List<Integer> insnRefs;
	private InputStream in;
	private PrintStream out;
	private int lineIndex;
	private boolean done;
	
	/**
	 * Initializes the program.
	 * @param input stream to use for Syscall.
	 * @param output stream to use for Syscall.
	 */
	public Program(InputStream input, PrintStream output) {
		regs = new RegisterFile();
		fpRegs = new FPRegisterFile();
		mem = new Memory(this, regs);
		lines = new ArrayList<>();
		lineRefs = new HashMap<>();
		insnRefs = new ArrayList<>();
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
	 * @return List containing all lines of the program, in order.
	 */
	public List<Line> getProgramLines() {
		return Collections.unmodifiableList(lines);
	}
	
	/**
	 * Adds a line of code to the program.
	 * @param newLine The line to add to the program.
	 */
	public void addLine(Line newLine) {
		lines.add(newLine);
		if(newLine.isExecutable()) 
			insnRefs.add(lines.size());
	}
	
	/**
	 * Adds a label to a line in the program.
	 * @param reference The String reference for the label.
	 * @param lineNum The line the label points to.
	 */
	public void addLabel(String reference, int lineNum) {
		lineRefs.put(reference, lineNum);
	}
	
	/**
	 * Jumps to 'main' section of program, initializing PC.
	 */
	public void start() {
		new Instruction(new Jump("j"), null, null, null, null, null, null,
				null, "main").execute(this);
	}
		
	/**
	 * Executes a jump to a given instruction target String.
	 * @param reference String target indicating the line to jump to.
	 */
	public void jump(String reference) {
		lineIndex = lineRefs.get(reference);
	}
	
	/**
	 * Gets the PC targeted by a given instruction target string.
	 * @param reference String target indicating the line to jump to.
	 * @return the PC of the line indicated.
	 */
	public int getTargetPC(String reference) {
		return Collections.binarySearch(insnRefs, lineRefs.get(reference))*4;
	}
	
	/**
	 * @return the current PC, address of next instruction to execute.
	 */
	public int getPC() {
		return Collections.binarySearch(insnRefs, lineIndex)*4;
	}
	
	/**
	 * Sets the current PC, address of next instruction to execute.
	 * @param newPC The new value of the PC.
	 */
	public void setPC(int newPC) {
		lineIndex = insnRefs.get(newPC/4);
	}
	
	/**
	 * @return The index of the currently executing line.
	 */
	public int getLineNum() {
		return lineIndex;
	}
	
	/**
	 * Sets the next program line number.
	 * @param num The next line to execute.
	 */
	public void setLineNum(int num) {
		lineIndex = num;
	}
	
	/**
	 * Gets the program line pointed to by the given PC.
	 * @param pc The PC.
	 * @return the index of the line.
	 */
	public int getLine(int pc) {
		return insnRefs.get(pc/4);
	}
	
	/**
	 * @return the next Line to execute.
	 */
	public Line getNextLine() {
		return lines.get(lineIndex++);
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
	
	/**
	 * Sets up lines of code added to the end of the program to properly exit.
	 * Used if program runs off bottom of file or exits via a return Jump Register.
	 */
	public void setupProgramClose() {
		regs.write(Register.ra, new Data((insnRefs.size() - 1)*4, Data.DataType.J_Target));
		addLine(new Line("", new Instruction(new AddImmediate("addi"), 
				Register.v0, Register.zero, null, null, null, null, 10, null)));
		addLine(new Line("", new Instruction(new Syscall("syscall"), 
				null, null, null, null, null, null, null, null)));
	}
	
	/**
	 * Loads immediates into instructions that used labels.
	 * Checks that all labels are valid.
	 * @throws LabelException if invalid label found
	 */
	public void loadLabels() throws LabelException {
		for(Line l : lines) {
			if(l.isExecutable()) {
				String label = l.getInstruction().getLabel();
				if(label != null && !lineRefs.containsKey(label)
						&& !getMem().isDataReference(label)) {
					throw new LabelException("No match for label " + label, l);
				}
				if(label != null && getMem().isDataReference(label)) {
					l.getInstruction().setImmediate(getMem().getMemoryAddress(label));
				}
			}
		}
	}

	/**
	 * Sets the calling conventions checker for current instructions 
	 * and the register file.  Does not set checker for instructions added later.
	 * Initializes process of checking calling conventions.
	 * @param checker
	 */
	public void checkCallingConventions(CallingConventionChecker checker) {
		regs.checkCallingConventions(checker);
		for(Line l : lines) {
			if(l.isExecutable()) l.getInstruction().checkCallingConventions(checker);
		}
	}

}
