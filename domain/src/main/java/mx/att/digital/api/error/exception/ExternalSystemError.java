package mx.att.digital.api.error.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import mx.att.digital.api.models.enums.ResultCode;

/**
 * The Class ExternalSystemError.
 */
@Getter
public class ExternalSystemError extends RuntimeException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The code. */
	private final String code = String.valueOf(ResultCode.OTHERS.getCode());
	
	/** The reason. */
	private final String reason = ResultCode.OTHERS.getMessage();

	/**
	 * Instantiates a new external system error.
	 *
	 * @param e the JsonProcessingException that caused this error
	 */
	public ExternalSystemError(JsonProcessingException e) {
		super(e);
	}
	/**
	 * Instantiates a new external system error with a custom message.
	 *
	 * @param msgError the custom error message
	 */
	public ExternalSystemError(String msgError) {
		super(msgError);
	}
}
