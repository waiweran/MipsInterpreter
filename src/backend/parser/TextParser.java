package backend.parser;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import backend.program.Program;
import exceptions.ProgramFormatException;

/**
 * Parses a text file and converts it into MIPS Instructions.
 * Stores instructions in a Program data structure.
 * @author Nathaniel
 * @version 11-04-2017
 */
public class TextParser {
	
	private Program prog;
	private InstructionParser insnParser;
	private DataParser dataParser;
	
	/**
	 * Initializes the Text Parser.
	 * @param code the File to parse.
	 * @param program the Program to add the parsed instructions to.
	 */
	public TextParser(Program program) {
		prog = program;
		insnParser = new InstructionParser(prog);
		dataParser = new DataParser(prog);
	}
	
	/**
	 * @return the Program the instructions are added to.
	 */
	public Program getProgram() {
		return prog;
	}
	
	/**
	 * Reads the given file.
	 * @param code File to read.
	 * @throws FileNotFoundException 
	 * @throws ProgramFormatException 
	 */
	public void readFile(File code) throws FileNotFoundException, ProgramFormatException {
		Scanner in = new Scanner(code);
		boolean insnSect = false;
		boolean dataSect = false;
		boolean hasInsns = false;
		while(in.hasNextLine()) {
			String lineText = in.nextLine();
			String text = lineText;
			if(lineText.indexOf('#') > 0) text = text.substring(0,  lineText.indexOf('#')); // Ignore Comments
			if(text.contains(".data")) {
				insnSect = false;
				dataSect = true;
				continue;
			}
			if(text.contains(".text")) {
				insnSect = true;
				dataSect = false;
				hasInsns = true;
				continue;
			}
			if(dataSect) dataParser.makeData(lineText);
			if(insnSect) insnParser.makeInstruction(lineText);
		}
		in.close();
		if(!hasInsns) {
			throw new ProgramFormatException("No instruction section found");
		}
	}
	
}
