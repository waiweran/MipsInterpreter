package exceptions;

/**
 * Exception class for incorrectly formatted instructions.
 * @author Nathaniel
 * @version 12-08-2017
 */
public class InstructionFormatException extends MIPSException {

	private static final long serialVersionUID = 1L;

	public InstructionFormatException(String message) {
		super(message);
	}

	public InstructionFormatException(String message, Throwable cause) {
		super(message, cause);
	}

}
