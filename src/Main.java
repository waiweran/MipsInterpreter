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
import backend.program.Program;
import backend.program.Register;
import backend.program.opcode.normal_mips.Jump;
import backend.program.opcode.normal_mips.Syscall;
import backend.program.opcode.specially_added.LoadImmediate;
import backend.state.Data;
import exceptions.DataFormatException;
import exceptions.ExecutionException;
import exceptions.InstructionFormatException;
import exceptions.JumpTargetException;
import frontend.MainGUI;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main class of MIPS Interpreter.
 * Runs the program.
 * @author Nathaniel
 * @version 11-17-2017
 */
public class Main extends Application {

	/**
	 * Entry point for the MIPS Interpreter.
	 * Runs in command line or as GUI.
	 * @param args command line arguments.
	 */
	public static void main(String[] args) {
		if(args.length == 0) {
			launch(args);
		}
		else {
			runFromTerminal(args);
		}
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		new MainGUI(primaryStage);
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
		try {
			try {
				new TextParser(progLoc, prog);
			} catch(JumpTargetException e) {
				throw new RuntimeException("Syntax Error: Jump Target", e);
			} catch(InstructionFormatException e) {
				throw new RuntimeException("Syntax Error: Instruction Section, Line " + 
						prog.getProgramLines().get(prog.getProgramLines().size() - 1), e);
			} catch (DataFormatException e) {
				throw new RuntimeException("Syntax Error: Data Section", e);
			}
			setupProgramClose(prog);
			new Instruction(new Jump(), null, null, null, null, null, null, 0, "main").execute(prog);
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
						throw new ExecutionException("Program line " + currentLine + 
								" caused exception", e);
					}
				}
			}
		} catch(FileNotFoundException e) {
			throw new RuntimeException("Program File not found", e);
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
				Data.DataType.Address));
		prog.getProgramLines().add(new Line("", new Instruction(new LoadImmediate(), 
				Register.v0, null, null, null, null, null, 10, "")));
		prog.getProgramLines().add(new Line("", new Instruction(new Syscall(), 
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
