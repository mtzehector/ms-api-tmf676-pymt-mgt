
package mx.att.digital.api.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JacksonConfigTest {
    @Test
    void objectMapperConfig() {
        JacksonConfig cfg = new JacksonConfig();
        ObjectMapper mapper = cfg.objectMapper();
        assertNotNull(mapper);
        assertEquals(JsonInclude.Include.NON_NULL, mapper.getSerializationConfig().getDefaultPropertyInclusion().getValueInclusion());
        assertFalse(mapper.getSerializationConfig().isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));
    }
}
