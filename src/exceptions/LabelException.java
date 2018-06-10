package exceptions;

import backend.program.Line;

/**
 * Exception class for incorrect or missing jump targets.
 * @author Nathaniel
 * @version 12-09-2017
 */
public class LabelException extends InstructionFormatException {

	private static final long serialVersionUID = 1L;

	public LabelException(String message, Line line) {
		super(message, line);
	}

	public LabelException(String message, Throwable cause, Line line) {
		super(message, cause, line);
	}

}
