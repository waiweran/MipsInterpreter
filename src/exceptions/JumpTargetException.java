package exceptions;

/**
 * Exception class for incorrect or missing jump targets.
 * @author Nathaniel
 * @version 12-09-2017
 */
public class JumpTargetException extends InstructionFormatException {

	private static final long serialVersionUID = 1L;

	public JumpTargetException(String message) {
		super(message);
	}

	public JumpTargetException(String message, Throwable cause) {
		super(message, cause);
	}

}
