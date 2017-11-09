package backend;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Program {
	
	private RegisterFile regs;
	private Memory mem;
	private List<Line> lines;
	private Map<String, Line> insnRefs;
	private Scanner inScan;
	private InputStream in;
	private PrintStream out;
	private int pc;
	private boolean done;

	
	public Program(InputStream input, PrintStream output) {
		regs = new RegisterFile();
		mem = new Memory(regs);
		lines = new ArrayList<>();
		insnRefs = new HashMap<>();
		inScan = new Scanner(input);
		in = input;
		out = output;
		done = false;
	}
	
	public RegisterFile getRegFile() {
		return regs;
	}
	
	public Memory getMem() {
		return mem;
	}
	
	public Map<String, Line> getInsnRefs() {
		return insnRefs;
	}
	
	public List<Line> getProgramLines() {
		return lines;
	}
		
	public void jump(String reference) {
		pc = lines.indexOf(insnRefs.get(reference));
	}
	
	public int getPC() {
		return pc;
	}
	
	public void setPC(int newPC) {
		pc = newPC;
	}
	
	public Line getNextLine() {
		return lines.get(pc++);
	}
	
	public boolean inputAvailable() {
		try {
			return in.available() > 0;
		} catch (IOException e) {
			return false;
		}
	}
	
	public String getInput() {
		return inScan.nextLine();
	}
	
	public PrintStream getOutput() {
		return out;
	}
	
	public void done() {
		done = true;
	}
	
	public boolean isDone() {
		return done;
	}

}
