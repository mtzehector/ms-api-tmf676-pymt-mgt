package mx.att.digital.api.util.exception;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class FieldFilteringExceptionTest {

    @Test
    void ctor_storesMessageAndCause() {
        Throwable cause = new IllegalArgumentException("bad");
        FieldFilteringException ex = new FieldFilteringException("oops", cause);
        assertEquals("oops", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }
}
