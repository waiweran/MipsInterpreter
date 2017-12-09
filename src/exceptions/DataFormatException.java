package exceptions;

/**
 * Exception class for incorrect formatting of global data.
 * @author Nathaniel
 * @version 12-08-2017
 */
public class DataFormatException extends ProgramFormatException {

	private static final long serialVersionUID = 1L;

	public DataFormatException(String message) {
		super(message);
	}

	public DataFormatException(String message, Throwable cause) {
		super(message, cause);
	}

}
