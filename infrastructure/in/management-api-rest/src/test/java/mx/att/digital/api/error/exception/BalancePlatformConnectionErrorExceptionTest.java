package mx.att.digital.api.error.exception;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClientException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BalancePlatformConnectionErrorExceptionTest {

    @Test
    void ctor_acceptsRestClientException() {
        RestClientException cause = new RestClientException("timeout");
        BalancePlatformConnectionErrorException ex = new BalancePlatformConnectionErrorException(cause);
        assertEquals(cause, ex.getCause());
        assertNotNull(ex.getCode());
        assertNotNull(ex.getReason());
    }
}
