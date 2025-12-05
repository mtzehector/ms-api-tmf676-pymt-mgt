package mx.att.digital.api.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import mx.att.digital.api.config.JacksonConfig;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;


/**
 * The Class JacksonConfigTest.
 */
class JacksonConfigTest {

    /**
     * Object mapper should be configured properly.
     */
    @Test
    void objectMapper_shouldBeConfiguredProperly() {
        // Arrange
        JacksonConfig config = new JacksonConfig();

        // Act
        ObjectMapper mapper = config.objectMapper();

        // Assert
        assertNotNull(mapper);
        assertFalse(mapper.getSerializationConfig().isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));
        assertEquals(JsonInclude.Include.NON_NULL, 
        		mapper.getSerializationConfig().getDefaultPropertyInclusion().getValueInclusion());
    }

    /**
     * Object mapper should serialize java time types.
     *
     * @throws Exception the exception
     */
    @Test
    void objectMapper_shouldSerializeJavaTimeTypes() throws Exception {
        ObjectMapper mapper = new JacksonConfig().objectMapper();

        LocalDate date = LocalDate.of(2023, 5, 15);
        String json = mapper.writeValueAsString(date);

        // JavaTimeModule ensures it's written as an ISO-8601 string, not timestamp
        assertEquals("\"2023-05-15\"", json);
    }
}

