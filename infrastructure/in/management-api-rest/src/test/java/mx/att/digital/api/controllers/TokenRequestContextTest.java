
package mx.att.digital.api.controllers;

import mx.att.digital.api.connectors.paymentsportal.TokenRequestPlaceholder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenRequestContextTest {

    @Test
    void setGetClear() {
        assertNull(TokenRequestContext.get());
        TokenRequestPlaceholder req = new TokenRequestPlaceholder();
        req.setUsername("u");
        TokenRequestContext.set(req);
        assertNotNull(TokenRequestContext.get());
        assertEquals("u", TokenRequestContext.get().getUsername());
        TokenRequestContext.clear();
        assertNull(TokenRequestContext.get());
    }
}
