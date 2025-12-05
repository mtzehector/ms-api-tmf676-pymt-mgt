package mx.att.digital.api.error.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;


/**
 * The Class DomainExceptionHandler.
 */
@RestControllerAdvice
public class DomainExceptionHandler {

	/** The Constant REASON. */
	public static final String REASON = "reason";
	
	/** The Constant DETAILS. */
	public static final String DETAILS = "details";
	
	/** The Constant CODE. */
	public static final String CODE = "code";

	/**
	 * Handle validation exceptions.
	 *
	 * @param ex the ex
	 * @return the response entity
	 */
	// Maneja errores de validación de @Valid en @RequestBody
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> fieldErrors = new HashMap<>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			fieldErrors.put(error.getField(), error.getDefaultMessage());
		}
		
		Map<String, Object> errors = new HashMap<>();
		errors.put(CODE, "30");
		errors.put(REASON, "Formato Invalido");
		errors.put(DETAILS, fieldErrors);

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
		error.put(CODE, ex.getCode());
		error.put(REASON, ex.getReason());
		error.put(DETAILS, ex.getDetails());

		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle all exceptions.
	 *
	 * @param ex the ex
	 * @return the response entity
	 */
	// Maneja otras excepciones no controladas
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex) {
		// Log at ERROR level for unexpected exceptions
		org.slf4j.LoggerFactory.getLogger(DomainExceptionHandler.class)
			.error("Unexpected error occurred", ex);
			
		Map<String, Object> error = new HashMap<>();
		error.put(CODE, "500");
		error.put(REASON, "Error interno del servidor");
		error.put(DETAILS, ex.getMessage());

		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
