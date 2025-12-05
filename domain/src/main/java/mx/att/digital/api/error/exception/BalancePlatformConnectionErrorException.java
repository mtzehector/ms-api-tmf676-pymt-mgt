package mx.att.digital.api.error.exception;

import lombok.Getter;
import mx.att.digital.api.models.enums.ResultCode;
import org.springframework.web.client.RestClientException;

/**
 * The Class BalancePlatformConnectionErrorException.
 */
@Getter
public class BalancePlatformConnectionErrorException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** The code. */
	private final String code = String.valueOf(ResultCode.FALLEN_AUTHORIZER.getCode());
	
	/** The reason. */
	private final String reason = String.valueOf(ResultCode.FALLEN_AUTHORIZER.getMessage());

	/**
	 * Instantiates a new balance platform connection error exception.
	 *
	 * @param e the RestClientException that caused this error
	 */
	public BalancePlatformConnectionErrorException(RestClientException e) {
		super(e);
	}

}
