import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import backend.assembler.Assembler;
import backend.debugger.CallingConventionChecker;
import backend.parser.TextParser;
import backend.program.Instruction;
import backend.program.Line;
import backend.program.Program;
import exceptions.DataFormatException;
import exceptions.ExecutionException;
import exceptions.InstructionFormatException;
import exceptions.LabelException;
import exceptions.ProgramFormatException;
import frontend.GUIStarter;
import terminal.Flag;
import terminal.FlagParser;

/**
 * Main class of MIPS Interpreter.
 * Runs the program.
 * @author Nathaniel
 * @version 11-17-2017
 */
public class Main {

	/**
	 * Entry point for the MIPS Interpreter.
	 * Runs in command line or as GUI.
	 * @param args command line arguments.
	 */
	public static void main(String[] args) {
		if(args.length == 0) {
			GUIStarter.runGUI(args);
		}
		else {
			runFromTerminal(args);
		}
	}
	
	/**
	 * Runs the MIPS Interpreter in the command line.
	 * @param args command line arguments specifying code file, I/O, run configurations.
	 */
	private static void runFromTerminal(String[] args) {
		int runs = -1;
		FlagParser flagparse = new FlagParser();
		flagparse.addFlag(new Flag("help", 'h'));
		flagparse.addFlag(new Flag("verbose", 'v'));
		flagparse.addFlag(new Flag("callcheck", 'c'));
		flagparse.addFlag(new Flag("endian"));
		flagparse.addFlag(new Flag("runs"));
		flagparse.parseFlags(args);
		List<String> arguments = flagparse.getArgs();
		
		if(flagparse.getFlag("help").isUsed()) {
			printHelp();
			return;
		}
		boolean checkCalls = flagparse.getFlag("callcheck").isUsed();
		boolean verbose = flagparse.getFlag("verbose").isUsed();
		Flag endian = flagparse.getFlag("endian");
		boolean bigEndian = endian.isUsed() && endian.getValue().equalsIgnoreCase("big");
		Flag runFlag = flagparse.getFlag("runs");
		if(runFlag.isUsed()) {
			try {
				runs = Integer.parseInt(runFlag.getValue());
			} catch (NumberFormatException e) {
				System.err.println("value passed with --runs flag must be an integer");
				return;
			}
		}
		
		if(arguments.isEmpty()) {
			System.err.println("No program file specified");
			return;
		}
		File progLoc = new File(arguments.get(0));
		File inFile = (arguments.size() > 1)? inFile = new File(arguments.get(1)) : null;
		File outFile = (arguments.size() > 2)? outFile = new File(arguments.get(2)) : null;
		
		try {
			Program prog = setupProgram(inFile, outFile);
			makeProgram(prog, bigEndian, progLoc);
			runProgram(prog, verbose, checkCalls, runs);
		} 
		catch(Exception e) {
			System.err.println(e.getMessage());
			return;
		}
	}
	
	/**
	 * Initializes the program inputs and outputs.
	 * @param inFile File to read input text from.
	 * @param outFile File to write output text to.
	 * @return the initialized program.
	 * @throws FileNotFoundException if the input or ouptut file could not be opened.
	 */
	private static Program setupProgram(File inFile, File outFile) throws FileNotFoundException {
		InputStream in = System.in;
		PrintStream out = System.out;
		if(inFile != null) {
			try {
				in = new FileInputStream(inFile);
			} catch (FileNotFoundException e) {
				throw new FileNotFoundException("Improper input file specified");
			}
		}		
		if(outFile != null) {
			try {
				out = new PrintStream(outFile);
			} catch (FileNotFoundException e) {
				throw new FileNotFoundException("Improper output file specified");
			}
		}
		Program prog = new Program(in, out);
		return prog;
	}

	/**
	 * Loads the instructions into the program from the file, sets up memory systems.
	 * @param prog The program to set up.
	 * @param bigEndian whether the system uses big or little endian memory.
	 * @param progFile the File holding the MIPS program.
	 * @throws FileNotFoundException if the program file could not be opened.
	 * @throws ProgramFormatException if there was a syntax error.
	 */
	private static void makeProgram(Program prog, boolean bigEndian, File progFile) 
			throws FileNotFoundException, ProgramFormatException {
		prog.getMem().setEndianness(bigEndian);
		try {
			new TextParser(prog).readFile(progFile);
			prog.setupProgramClose();
			Assembler assemble = new Assembler(prog);
			int insnNum = 0;
			for(Line l : prog.getProgramLines()) {
				if(l.isExecutable()) assemble.assemble(l, insnNum++);
			}
			prog.loadLabels();
		} catch(FileNotFoundException e) {
			throw new FileNotFoundException("Program File not found");
		} catch(LabelException e) {
			throw new LabelException("Syntax Error: Label, line "
					+ e.getLine(), e, e.getLine());
		} catch(InstructionFormatException e) {
			throw new InstructionFormatException("Syntax Error: Instruction Section, line "
					+ e.getLine(), e, e.getLine());
		} catch (DataFormatException e) {
			throw new DataFormatException("Syntax Error: Data Section", e);
		} catch (ProgramFormatException e) {
			throw new ProgramFormatException("Syntax Error: General", e);
		}
	}
	
	/**
	 * Runs the program simulation.
	 * @param prog the program to simulate.
	 * @param verbose whether instructions are printed as they run.
	 * @param checkCalls whether correct stack saving is used in procedure calls.
	 * @param runs maximum number of instructions to run before exiting.
	 */
	private static void runProgram(Program prog, boolean verbose, boolean checkCalls, int runs) {
		CallingConventionChecker callChecker = 
				new CallingConventionChecker(prog.getRegFile());
		if(checkCalls) {
			prog.checkCallingConventions(callChecker);
		}
		prog.start();
		int lastPC = -1;
		for(int i = 0; !prog.isDone() && i != runs; i++) {
			Line currentLine = prog.getNextLine();
			if(verbose && lastPC != prog.getPC()) 
				System.out.println(currentLine);
			if(lastPC == prog.getPC()) i--;
			lastPC = prog.getPC();
			if(currentLine.isExecutable()) {
				try {
					Instruction insn = currentLine.getInstruction();
					insn.execute(prog);
				}
				catch(Exception e) {
					throw new ExecutionException("Program line " + currentLine + 
							" caused exception", e);
				}
			}
		}
		if(checkCalls) {
			System.err.println("\n\nFound " + callChecker.getNumViolations() 
			+ " violations of calling conventions.");
		}
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
