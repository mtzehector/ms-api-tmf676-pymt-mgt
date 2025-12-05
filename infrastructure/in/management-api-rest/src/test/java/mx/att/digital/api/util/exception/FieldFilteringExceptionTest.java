
package mx.att.digital.api.util.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FieldFilteringExceptionTest {
    @Test
    void ctor() {
        FieldFilteringException ex = new FieldFilteringException("msg", new RuntimeException("x"));
        assertEquals("msg", ex.getMessage());
        assertNotNull(ex.getCause());
    }
}
