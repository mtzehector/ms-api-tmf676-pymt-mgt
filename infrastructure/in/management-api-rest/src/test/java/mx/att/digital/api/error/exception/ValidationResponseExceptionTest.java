package mx.att.digital.api.error.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationResponseExceptionTest {

    @Test
    void getters_shouldExposeValues() {
        ValidationResponseException ex = new ValidationResponseException("400", "Bad Request", "missing field");
        assertEquals("400", ex.getCode());
        assertEquals("Bad Request", ex.getReason());
        assertEquals("missing field", ex.getDetails());
        assertTrue(ex.getMessage().contains("Bad Request"));
    }
}
