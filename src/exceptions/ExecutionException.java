package exceptions;

public class ExecutionException extends MIPSException {

	private static final long serialVersionUID = 1L;

	public ExecutionException(String message) {
		super(message);
	}

	public ExecutionException(String message, Throwable cause) {
		super(message, cause);
	}

}
