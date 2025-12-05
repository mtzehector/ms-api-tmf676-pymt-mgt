package mx.att.digital.api.error.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExternalSystemErrorTest {

    @Test
    void stringCtor_setsMessageAndHasCodeReason() {
        String msg = "Upstream down";
        ExternalSystemError err = new ExternalSystemError(msg);
        assertEquals(msg, err.getMessage());
        // code y reason son constantes derivadas de ResultCode en la clase
        assertNotNull(err.getCode());
        assertNotNull(err.getReason());
        assertTrue(err.toString().contains("Upstream"));
    }
}
