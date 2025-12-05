package mx.att.digital.api.error.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * The Class ExternalSystemErrorTest.
 */
class ExternalSystemErrorTest {

    /**
     * Test constructor and message.
     */
    @Test
    void testConstructorAndMessage() {
        String errorMessage = "External system failure";
        ExternalSystemError error = new ExternalSystemError(errorMessage);

        // Verifica que el mensaje del error sea el esperado
        assertEquals(errorMessage, error.getMessage());

        // Verifica que la clase sea una instancia de RuntimeException
        assertInstanceOf(RuntimeException.class, error);
    }

    /**
     * (Opcional) Testeamos también el constructor que recibe JsonProcessingException.
     */
    @Test
    void testConstructorWithJsonProcessingException() {
        JsonProcessingException cause = new JsonProcessingException("json error") {};
        ExternalSystemError error = new ExternalSystemError(cause);

        assertEquals(cause, error.getCause());
        assertInstanceOf(RuntimeException.class, error);
    }
}
