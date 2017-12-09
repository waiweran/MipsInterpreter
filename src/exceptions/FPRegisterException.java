package exceptions;

/**
 * Exception class for problems caused by improper use of FP Registers.
 * @author Nathaniel
 * @version 12-08-2017
 */
public class FPRegisterException extends ExecutionException {

	private static final long serialVersionUID = 1L;

	public FPRegisterException(String message) {
		super(message);
	}

	public FPRegisterException(String message, Throwable cause) {
		super(message, cause);
	}

}
