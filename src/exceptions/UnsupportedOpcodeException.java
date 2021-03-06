package exceptions;

/**
 * Exception class for problems caused by opcodes that are not supported.
 * @author Nathaniel
 * @version 12-08-2017
 */
public class UnsupportedOpcodeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UnsupportedOpcodeException(String message) {
		super(message);
	}

	public UnsupportedOpcodeException(String message, Throwable cause) {
		super(message, cause);
	}

}
