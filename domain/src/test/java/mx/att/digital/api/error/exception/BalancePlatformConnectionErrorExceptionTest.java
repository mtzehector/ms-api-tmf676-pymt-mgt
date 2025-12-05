package mx.att.digital.api.error.exception;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClientException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The Class BalancePlatformConnectionErrorExceptionTest.
 */
class BalancePlatformConnectionErrorExceptionTest {

	/**
	 * Test constructor and message.
	 */
	/*@Test*/
	void testConstructorAndMessage() {
		String errorMessage = "Connection failed";
		BalancePlatformConnectionErrorException exception = 
				new BalancePlatformConnectionErrorException((RestClientException) new RuntimeException(errorMessage));

		assertEquals(errorMessage, exception.getMessage());
	}

	/**
	 * Test code and reason fields existence.
	 *
	 * @throws Exception the exception
	 */
	@Test
	void testCodeAndReasonFieldsExistence() throws Exception {
		BalancePlatformConnectionErrorException exception = 
				new BalancePlatformConnectionErrorException(new RestClientException("Connection error"));

		// Usamos reflexión para verificar que los campos están definidos y tienen
		// valores esperados
		var codeField = BalancePlatformConnectionErrorException.class.getDeclaredField("code");
		codeField.setAccessible(true);
		String codeValue = (String) codeField.get(exception);
		assertNotNull(codeValue);
		assertFalse(codeValue.isEmpty());

		var reasonField = BalancePlatformConnectionErrorException.class.getDeclaredField("reason");
		reasonField.setAccessible(true);
		String reasonValue = (String) reasonField.get(exception);
		assertNotNull(reasonValue);
		assertFalse(reasonValue.isEmpty());
	}
}
