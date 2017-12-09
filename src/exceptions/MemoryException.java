package exceptions;

/**
 * Exception class for problems caused bad memory accesses.
 * @author Nathaniel
 * @version 12-08-2017
 */
public class MemoryException extends ExecutionException {

	private static final long serialVersionUID = 1L;

	public MemoryException(String message) {
		super(message);
	}

	public MemoryException(String message, Throwable cause) {
		super(message, cause);
	}

}
