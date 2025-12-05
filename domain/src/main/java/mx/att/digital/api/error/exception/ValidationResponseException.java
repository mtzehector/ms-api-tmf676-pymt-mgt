package mx.att.digital.api.error.exception;

import lombok.Getter;


/**
 * The Class ValidationResponseException.
 */
@Getter
public class ValidationResponseException extends RuntimeException {

	/** Serial version UID. */
	private static final long serialVersionUID = 1L;
	
	/** The code. */
	private final String code;
	
	/** The reason. */
	private final String reason;
	
	/** The details. */
	private final String details;

	/**
	 * Instantiates a new validation response exception type NullPointerException.
	 *
	 * @param code the code
	 * @param reason the reason
	 * @param details the details
	 * @param cause the underlying exception/NullPointerException that triggered this validation error; 
	 * used to preserve
	 *                the original stack trace for better error tracking
	 */
	public ValidationResponseException(String code, String reason, String details, 
			NullPointerException cause) {
		super(reason, cause); // Llamada explícita al constructor de RuntimeException con el mensaje
		this.code = code;
		this.reason = reason;
		this.details = details;
	}

	/**
	 * Constructs a new ValidationResponseException type ReflectiveOperationException 
	 * with the specified code, reason, details, and cause.
	 * 
	 * @param code    the error code representing the specific validation error
	 * @param reason  a brief explanation of the validation failure
	 * @param details additional context or information about the error
	 * @param cause   the underlying exception/ReflectiveOperationException that triggered 
	 * this validation error; used to preserve
	 *                the original stack trace for better error tracking
	 */
	public ValidationResponseException(String code, String reason, 
			String details, ReflectiveOperationException cause) {
		super(reason, cause); // Llamada explícita al constructor de RuntimeException con el mensaje
		this.code = code;
		this.reason = reason;
		this.details = details;
	}

	/**
	 * Instantiates a new validation response exception simple.
	 *
	 * @param code the code
	 * @param reason the reason
	 * @param details the details
	 */
	public ValidationResponseException(String code, String reason, 
			String details) {
		super(reason); // Llamada explícita al constructor de RuntimeException con el mensaje
		this.code = code;
		this.reason = reason;
		this.details = details;
	}
}
