import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import backend.Instruction;
import backend.Line;
import backend.Opcode;
import backend.Program;
import backend.Register;
import backend.TextParser;

public class MainTerminal {

	public static void main(String[] args) {
		List<String> arguments = Arrays.asList(args);
		if(arguments.get(0).equals("-h") || arguments.get(0).equals("--help")) {
			printHelp();
			return;
		}
		boolean verbose = arguments.get(0).equals("-v") || arguments.get(0).equals("--verbose");
		if(verbose) arguments.remove(0);
		File progLoc = new File(arguments.remove(0));
		if(!arguments.isEmpty() && arguments.get(arguments.size() - 1).matches("\\d+")) {
			Integer.parseInt(arguments.remove(arguments.size() - 1));
		}
		Program prog = makeProgram(arguments);
		int runs = -1;
		TextParser parser = new TextParser(progLoc, prog);
		prog = parser.getProgram();
		setupProgramClose(prog);
		new Instruction(Opcode.Jump, null, null, null, 0, "main").execute(prog);
		for(int i = 0; !prog.isDone() && i != runs; i++) {
			Line currentLine = prog.getNextLine();
			if(verbose) System.out.println(currentLine);
			if(currentLine.isExecutable()) {
				try {
					currentLine.getInstruction().execute(prog);
				}
				catch(Exception e) {
					throw new RuntimeException("Program line " + currentLine + 
							" caused exception", e);
				}
			}
		}
	}

	private static Program makeProgram(List<String> arguments) {
		InputStream in = System.in;
		PrintStream out = System.out;
		if(!arguments.isEmpty()) {
			try {
				in = new FileInputStream(new File(arguments.remove(0)));
			} catch (FileNotFoundException e) {
				throw new RuntimeException("Improper in/out file");
			}
		}		
		if(!arguments.isEmpty()) {
			try {
				out = new PrintStream(new File(arguments.remove(0)));
			} catch (FileNotFoundException e) {
				throw new RuntimeException("Improper in/out file");
			}
		}
		Program prog = new Program(in, out);
		return prog;
	}

	private static void setupProgramClose(Program prog) {
		prog.getRegFile().write(Register.ra, prog.getProgramLines().size());
		prog.getProgramLines().add(new Line("", new Instruction(Opcode.LoadImmediate, 
				Register.v0, null, null, 10, "")));
		prog.getProgramLines().add(new Line("", new Instruction(Opcode.Syscall, 
				null, null, null, 0, "")));
	}
	
	private static void printHelp() {
		System.out.println("MIPS I Interpreter\n");
		System.out.println("See GitHub page for more information\n");
		System.out.println("Command Line Arguments\n");
		System.out.println("Flags: -h, -v, --help, --verbose");
		System.out.println("\t-v, --verbose prints program lines as they execute");
		System.out.println("Argument 1: program file");
		System.out.println("Argument 2 (Optional): console input file");
		System.out.println("Argument 3 (Optional, only available if Argument 2 "
				+ "present): console output file");
		System.out.println("Argument 4 (Optional, other arguments not "
				+ "required): max instructions to execute");
	}

}
