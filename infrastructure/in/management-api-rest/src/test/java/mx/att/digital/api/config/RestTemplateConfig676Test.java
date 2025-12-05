
package mx.att.digital.api.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;

import static org.junit.jupiter.api.Assertions.assertThrows;

class RestTemplateConfig676Test {

    @Test
    @DisplayName("restTemplate676() lanza IllegalStateException cuando el trustStore es inválido")
    void restTemplate_invalidTrustStore() throws Exception {
        RestTemplateConfig676 cfg = new RestTemplateConfig676();
        // Solo existe trustStore/trustStorePassword en la clase
        setField(cfg, "trustStore", new ByteArrayResource(new byte[0]) );
        setField(cfg, "trustStorePassword", "pass");

        assertThrows(IllegalStateException.class, cfg::restTemplate676);
    }

    private static void setField(Object target, String name, Object value) throws Exception {
        var f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }
}
