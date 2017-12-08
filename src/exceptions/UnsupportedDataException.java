package exceptions;

/**
 * Exception class for problems caused by use of data types not supported.
 * @author Nathaniel
 * @version 12-08-2017
 */
public class UnsupportedDataException extends DataFormatException {

	private static final long serialVersionUID = 1L;

	public UnsupportedDataException(String message) {
		super(message);
	}

	public UnsupportedDataException(String message, Throwable cause) {
		super(message, cause);
	}

}
