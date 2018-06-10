package exceptions;

import backend.program.Line;

/**
 * Exception class for incorrectly formatted instructions.
 * @author Nathaniel
 * @version 12-08-2017
 */
public class InstructionFormatException extends ProgramFormatException {

	private static final long serialVersionUID = 1L;
	
	private Line causeLine;

	public InstructionFormatException(String message, Line line) {
		super(message);
		causeLine = line;
	}

	public InstructionFormatException(String message, Throwable cause, Line line) {
		super(message, cause);
		causeLine = line;
	}
	
	public Line getLine() {
		return causeLine;
	}

}
