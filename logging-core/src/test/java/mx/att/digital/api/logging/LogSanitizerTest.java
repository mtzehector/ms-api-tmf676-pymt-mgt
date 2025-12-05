package mx.att.digital.api.logging;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class LogSanitizerTest {

    @Test
    void sanitizeException_returnsClassAndMessage() {
        RuntimeException ex = new RuntimeException("boom");
        String sanitized = LogSanitizer.sanitizeException(ex);
        assertTrue(sanitized.contains("RuntimeException"));
        assertTrue(sanitized.contains("boom"));
    }

    @Test
    void sanitizeException_null_returnsNull() {
        assertNull(LogSanitizer.sanitizeException(null));
    }
}
