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

	/**
	 * Handle connection error exception.
	 *
	 * @param balancePlatformConnectionErrorException the balance platform connection error exception
	 * @return the response entity
	 */
	@ExceptionHandler({ BalancePlatformConnectionErrorException.class })
	public ResponseEntity<ErrorResponse> handleConnectionErrorException(
			BalancePlatformConnectionErrorException balancePlatformConnectionErrorException) {
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
		Map<String, Object> errors = new HashMap<>();
		errors.put("code", "30");
		errors.put("reason", "Formato Invalido");

		Map<String, String> fieldErrors = new HashMap<>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			fieldErrors.put(error.getField(), error.getDefaultMessage());
		}
		errors.put("details", fieldErrors);

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
		Map<String, Object> error = new HashMap<>();
		error.put("code", ex.getCode());
		error.put("reason", ex.getReason());
		error.put("details", ex.getDetails());

		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

}
