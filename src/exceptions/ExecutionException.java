package exceptions;

/**
 * Exception class for problems caused by runtime errors in MIPS Code.
 * @author Nathaniel
 * @version 12-08-2017
 */
public class ExecutionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ExecutionException(String message) {
		super(message);
	}

	public ExecutionException(String message, Throwable cause) {
		super(message, cause);
	}

}
