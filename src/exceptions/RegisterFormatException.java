package exceptions;

/**
 * Exception class for incorrectly formatted register names.
 * @author Nathaniel
 * @version 12-09-2017
 */
public class RegisterFormatException extends ProgramFormatException {

	private static final long serialVersionUID = 1L;

	public RegisterFormatException(String message) {
		super(message);
	}

	public RegisterFormatException(String message, Throwable cause) {
		super(message, cause);
	}

}
