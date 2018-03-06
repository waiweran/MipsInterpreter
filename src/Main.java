import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import backend.TextParser;
import backend.debugger.StackUsage;
import backend.program.Instruction;
import backend.program.Line;
import backend.program.Program;
import backend.program.opcode.normal_mips.Jump;
import backend.program.opcode.normal_mips.JumpRegister;
import exceptions.DataFormatException;
import exceptions.ExecutionException;
import exceptions.InstructionFormatException;
import exceptions.JumpTargetException;
import exceptions.ProgramFormatException;
import frontend.MainGUI;
import javafx.application.Application;
import javafx.stage.Stage;
import terminal.FlagParser;

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
		FlagParser flagparse = new FlagParser(args);
		if(flagparse.hasFlag("help")) {
			printHelp();
			return;
		}
		boolean verbose = flagparse.hasFlag("verbose");
		boolean stackCheck = flagparse.hasFlag("stackcheck");
		for(int i = arguments.size() - 1; i >= 0; i--) {
			if(arguments.get(i).startsWith("-")){
				arguments.remove(i);
			}
		}
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
			} catch (ProgramFormatException e) {
				throw new RuntimeException("Syntax Error: General", e);
			}
			StackUsage stackChecker = new StackUsage(prog.getRegFile());
			prog.setupProgramClose();
			if(stackCheck) stackChecker.startProcedureCheck();
			new Instruction(new Jump(), null, null, null, null, null, null, 0, "main").execute(prog);
			int lastPC = -1;
			for(int i = 0; !prog.isDone() && i != runs; i++) {
				Line currentLine = prog.getNextLine();
				if(verbose && lastPC != prog.getPC()) System.out.println(currentLine);
				if(lastPC == prog.getPC()) i--;
				lastPC = prog.getPC();
				if(currentLine.isExecutable()) {
					try {
						Instruction insn = currentLine.getInstruction();
						insn.execute(prog);
						if(stackCheck) {
							if(insn.getOpcode().getClass().getName().endsWith("AndLink")) {
								stackChecker.startProcedureCheck();
							}
							if(insn.getOpcode() instanceof JumpRegister) {
								if(!stackChecker.endProcedureCheck()) {
									System.out.println("Improper Stack Usage Detected");
								}
							}
						}
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
	 * Prints the Help text.
	 */
	private static void printHelp() { 
		try {
			Scanner in = new Scanner(new File("README.md"));
			while(in.hasNextLine()) {
				System.out.println(in.nextLine());
			}
			in.close();
		} catch (FileNotFoundException e) {
			System.out.println("See Github page for details");
		}
		
	}

}
