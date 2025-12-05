package mx.att.digital.api.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Class ErrorResponse.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor

/**
 * The Class ErrorResponseBuilder.
 */
@Builder
public class ErrorResponse {

	/** The message. */
	private String message;
	
	/** The reason. */
	private String reason;
	
	/** The code. */
	private String code;

}
