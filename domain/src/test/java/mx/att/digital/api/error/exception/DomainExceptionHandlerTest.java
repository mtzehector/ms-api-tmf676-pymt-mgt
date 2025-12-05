package mx.att.digital.api.error.exception;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * The Class DomainExceptionHandlerTest.
 */
class DomainExceptionHandlerTest {

	/** The handler. */
	private DomainExceptionHandler handler;

	/** The closeable. */
	private AutoCloseable closeable;

	/** The method argument not valid exception. */
	@Mock
	private MethodArgumentNotValidException methodArgumentNotValidException;

	/** The binding result. */
	@Mock
	private BindingResult bindingResult;

	/**
	 * Setup.
	 */
	@BeforeEach
	void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		handler = new DomainExceptionHandler();
	}

	/**
	 * Tear down.
	 *
	 * @throws Exception the exception
	 */
	@AfterEach
	void tearDown() throws Exception {
		closeable.close();
	}

	/**
	 * Test handle validation exceptions.
	 */
	@Test
	void testHandleValidationExceptions() {
		// Simulamos errores de campo
		FieldError fieldError1 = new FieldError("objectName", "field1", "must not be null");
		FieldError fieldError2 = new FieldError("objectName", "field2", "size must be between 1 and 10");

		List<FieldError> fieldErrors = Arrays.asList(fieldError1, fieldError2);

		when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
		when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

		ResponseEntity<Map<String, Object>> response = handler
				.handleValidationExceptions(methodArgumentNotValidException);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

		Map<String, Object> body = response.getBody();
		assertNotNull(body);
		assertEquals("30", body.get("code"));
		assertEquals("Formato Invalido", body.get("reason"));

		@SuppressWarnings("unchecked")
		Map<String, String> details = (Map<String, String>) body.get("details");
		assertEquals(2, details.size());
		assertEquals("must not be null", details.get("field1"));
		assertEquals("size must be between 1 and 10", details.get("field2"));
	}

	/**
	 * Test handle validation exception.
	 */
	@Test
	void testHandleValidationException() {
		ValidationResponseException ex = new ValidationResponseException("20", "Invalid Data", "invalid");

		ResponseEntity<Map<String, Object>> response = handler.handleValidationException(ex);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

		Map<String, Object> body = response.getBody();
		assertNotNull(body);
		assertEquals("20", body.get("code"));
		assertEquals("Invalid Data", body.get("reason"));
		assertEquals("invalid", body.get("details"));
	}

	/**
	 * Test handle all exceptions.
	 */
	@Test
	void testHandleAllExceptions() {
		Exception ex = new Exception("Some internal error");

		ResponseEntity<Map<String, Object>> response = handler.handleAllExceptions(ex);

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

		Map<String, Object> body = response.getBody();
		assertNotNull(body);
		assertEquals("500", body.get("code"));
		assertEquals("Error interno del servidor", body.get("reason"));
		assertEquals("Some internal error", body.get("details"));
	}
}
