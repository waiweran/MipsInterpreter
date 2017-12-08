package exceptions;

/**
 * Exception class for problems caused by MIPS code.
 * @author Nathaniel
 * @version 12-08-2017
 */
public class MIPSException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MIPSException(String message) {
		super(message);
	}

	public MIPSException(String message, Throwable cause) {
		super(message, cause);
	}

}
