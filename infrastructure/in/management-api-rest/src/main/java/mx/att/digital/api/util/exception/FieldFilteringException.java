package mx.att.digital.api.util.exception;

/**
 * The Class FieldFilteringException.
 */
public class FieldFilteringException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new field filtering exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public FieldFilteringException(String message, Throwable cause) {
		super(message, cause);
	}
}
