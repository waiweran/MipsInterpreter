import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
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
		List<String> arguments = new ArrayList<>();
		int runs = -1;
		arguments.addAll(Arrays.asList(args));
		FlagParser flagparse = new FlagParser(args);
		if(flagparse.hasFlag("help")) {
			printHelp();
			return;
		}
		boolean verbose = flagparse.hasFlag("verbose");
		boolean checkCalls = flagparse.hasFlag("callcheck");
		boolean bigEndian = flagparse.hasFlag("bigendian");
		if(bigEndian && flagparse.hasFlag("littleendian")) {
			System.err.println("Cannot have big endian and little endian at same time");
			return;
		}
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
		prog.getMem().setEndianness(bigEndian);
		try {
			try {
				new TextParser(prog).readFile(progLoc);
				prog.setupProgramClose();
				Assembler assemble = new Assembler(prog);
				int insnNum = 0;
				for(Line l : prog.getProgramLines()) {
					if(l.isExecutable()) assemble.assemble(l, insnNum++);
				}
				prog.loadLabels();
			} catch(LabelException e) {
				throw new RuntimeException("Syntax Error: Label, line "
						+ e.getLine(), e);
			} catch(InstructionFormatException e) {
				throw new RuntimeException("Syntax Error: Instruction Section, line "
						+ e.getLine(), e);
			} catch (DataFormatException e) {
				throw new RuntimeException("Syntax Error: Data Section", e);
			} catch (ProgramFormatException e) {
				throw new RuntimeException("Syntax Error: General", e);
			}
			CallingConventionChecker callChecker = 
					new CallingConventionChecker(prog.getRegFile());
			if(checkCalls) {
				prog.checkCallingConventions(callChecker);
			}
			prog.start();
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
					}
					catch(Exception e) {
						throw new ExecutionException("Program line " + currentLine + 
								" caused exception", e);
					}
				}
			}
			if(checkCalls) {
				System.out.println("\n\nFound " + callChecker.getNumViolations() 
						+ " violations of calling conventions.");
			}
		} catch(FileNotFoundException e) {
			throw new RuntimeException("Program File not found");
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
				throw new RuntimeException("Improper input file specified");
			}
		}		
		if(!arguments.isEmpty()) {
			try {
				out = new PrintStream(new File(arguments.remove(0)));
			} catch (FileNotFoundException e) {
				throw new RuntimeException("Improper output file specified");
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
