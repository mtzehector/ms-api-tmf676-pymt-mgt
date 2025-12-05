package mx.att.digital.api.handlers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.att.digital.api.error.ErrorResponse;
import mx.att.digital.api.error.exception.BalancePlatformConnectionErrorException;
import mx.att.digital.api.error.exception.ExternalSystemError;
import mx.att.digital.api.error.exception.ValidationResponseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * The Class ControllerAdvice.
 */
@RestControllerAdvice
@Slf4j
@AllArgsConstructor
public class ControllerAdvice {

	private static final String ERROR_CODE_VALIDATION = "30";
	private static final String ERROR_REASON_INVALID_FORMAT = "Formato Invalido";
	private static final String FIELD_CODE = "code";
	private static final String FIELD_REASON = "reason";
	private static final String FIELD_DETAILS = "details";

	/**
	 * Handle connection error exception.
	 *
	 * @param balancePlatformConnectionErrorException the balance platform connection error exception
	 * @return the response entity
	 */
	@ExceptionHandler({ BalancePlatformConnectionErrorException.class })
	public ResponseEntity<ErrorResponse> handleConnectionErrorException(
			BalancePlatformConnectionErrorException balancePlatformConnectionErrorException) {
		log.error("Balance platform connection error: code={}, reason={}", 
				balancePlatformConnectionErrorException.getCode(),
				balancePlatformConnectionErrorException.getReason(), 
				balancePlatformConnectionErrorException);
		ErrorResponse errorResponse = ErrorResponse.builder()
				.message(balancePlatformConnectionErrorException.getMessage())
				.reason(balancePlatformConnectionErrorException.getReason())
				.code(balancePlatformConnectionErrorException.getCode()).build();
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
	}

	/**
	 * Handle external system error.
	 *
	 * @param externalSystemError the external system error
	 * @return the response entity
	 */
	@ExceptionHandler({ ExternalSystemError.class })
	public ResponseEntity<ErrorResponse> handleExternalSystemError(ExternalSystemError externalSystemError) {
		log.error("External system error: code={}, reason={}", 
				externalSystemError.getCode(),
				externalSystemError.getReason(), 
				externalSystemError);
		ErrorResponse errorResponse = ErrorResponse.builder().message(externalSystemError.getMessage())
				.reason(externalSystemError.getReason()).code(externalSystemError.getCode()).build();
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	}

	/**
	 * Handle validation exceptions.
	 *
	 * @param ex the ex
	 * @return the response entity
	 */
	// Maneja errores de validación de @Valid en @RequestBody
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
		log.warn("Validation failed: {}", ex.getBindingResult().getFieldErrors());
		Map<String, Object> errors = new HashMap<>();
		errors.put(FIELD_CODE, ERROR_CODE_VALIDATION);
		errors.put(FIELD_REASON, ERROR_REASON_INVALID_FORMAT);

		Map<String, String> fieldErrors = new HashMap<>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			fieldErrors.put(error.getField(), error.getDefaultMessage());
		}
		errors.put(FIELD_DETAILS, fieldErrors);

		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle validation exception.
	 *
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler(ValidationResponseException.class)
	public ResponseEntity<Map<String, Object>> handleValidationException(ValidationResponseException ex) {
		log.warn("Validation response exception: code={}, reason={}", ex.getCode(), ex.getReason(), ex);
		Map<String, Object> error = new HashMap<>();
		error.put(FIELD_CODE, ex.getCode());
		error.put(FIELD_REASON, ex.getReason());
		error.put(FIELD_DETAILS, ex.getDetails());

		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

}
