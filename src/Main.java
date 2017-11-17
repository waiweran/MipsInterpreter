import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import backend.TextParser;
import backend.program.Instruction;
import backend.program.Line;
import backend.program.Opcode;
import backend.program.Program;
import backend.program.Register;
import backend.state.Data;
import frontend.MainGUI;

public class Main {

	/**
	 * Entry point for the MIPS Interpreter.
	 * Runs in command line or as GUI.
	 * @param args command line arguments.
	 */
	public static void main(String[] args) {
		if(args.length == 0) {
			MainGUI.launch(args);
		}
		else {
			runFromTerminal(args);
		}
	}
	
	/**
	 * Runs the MIPS Interpreter in the command line.
	 * @param args command line arguments specifying code file, I/O, run configurations.
	 */
	public static void runFromTerminal(String[] args) {
		List<String> arguments = new ArrayList<>();
		int runs = -1;
		arguments.addAll(Arrays.asList(args));
		if(arguments.get(0).equals("-h") || arguments.get(0).equals("--help")) {
			printHelp();
			return;
		}
		boolean verbose = arguments.get(0).equals("-v") || arguments.get(0).equals("--verbose");
		if(verbose) arguments.remove(0);
		File progLoc = new File(arguments.remove(0));
		if(!arguments.isEmpty() && arguments.get(arguments.size() - 1).matches("\\d+")) {
			runs = Integer.parseInt(arguments.remove(arguments.size() - 1));
		}
		Program prog = makeProgram(arguments);
		TextParser parser = new TextParser(progLoc, prog);
		prog = parser.getProgram();
		setupProgramClose(prog);
		new Instruction(Opcode.Jump, null, null, null, null, null, null, 0, "main").execute(prog);
		int lastPC = -1;
		for(int i = 0; !prog.isDone() && i != runs; i++) {
			Line currentLine = prog.getNextLine();
			if(verbose && lastPC != prog.getPC()) System.out.println(currentLine);
			if(lastPC == prog.getPC()) i--;
			lastPC = prog.getPC();
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

	/**
	 * Makes a Program object from specified code file.
	 * @param arguments command line arguments configuring program.
	 * @return the Program.
	 */
	private static Program makeProgram(List<String> arguments) {
		InputStream in = System.in;
		PrintStream out = System.out;
		if(!arguments.isEmpty()) {
			try {
				in = new FileInputStream(new File(arguments.remove(0)));
			} catch (FileNotFoundException e) {
				throw new RuntimeException("Improper input file");
			}
		}		
		if(!arguments.isEmpty()) {
			try {
				out = new PrintStream(new File(arguments.remove(0)));
			} catch (FileNotFoundException e) {
				throw new RuntimeException("Improper output file");
			}
		}
		Program prog = new Program(in, out);
		return prog;
	}

	/**
	 * Sets up lines of code added to the end of the program to properly exit.
	 * Used if program runs off bottom of file or exits via a return Jump Register.
	 * @param prog the Program to set up closing for.
	 */
	private static void setupProgramClose(Program prog) {
		prog.getRegFile().write(Register.ra, new Data(prog.getProgramLines().size(), 
				Data.DataType.Address, Data.Permissions.Read_Only));
		prog.getProgramLines().add(new Line("", new Instruction(Opcode.LoadImmediate, 
				Register.v0, null, null, null, null, null, 10, "")));
		prog.getProgramLines().add(new Line("", new Instruction(Opcode.Syscall, 
				null, null, null, null, null, null, 0, "")));
	}
	
	/**
	 * Prints the Help text.
	 */
	private static void printHelp() { //TODO make this a file
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