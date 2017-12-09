package exceptions;

/**
 * Exception class for incorrect formatting.
 * @author Nathaniel
 * @version 12-09-2017
 */
public class ProgramFormatException extends MIPSException {

	private static final long serialVersionUID = 1L;

	public ProgramFormatException(String message) {
		super(message);
	}

	public ProgramFormatException(String message, Throwable cause) {
		super(message, cause);
	}

}
