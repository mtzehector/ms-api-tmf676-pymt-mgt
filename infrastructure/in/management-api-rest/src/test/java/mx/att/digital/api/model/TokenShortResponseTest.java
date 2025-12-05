
package mx.att.digital.api.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenShortResponseTest {

    @Test
    void gettersSettersAndJson() throws Exception {
        TokenShortResponse resp = new TokenShortResponse();
        resp.setToken("TKN");
        resp.setMessage(null);

        assertEquals("TKN", resp.getToken());
        assertNull(resp.getMessage());

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        String json = mapper.writeValueAsString(resp);

        // En Java, un literal de comillas dobles se escapa con "
        assertTrue(json.contains("\"token\":\"TKN\""));
        // message puede ser null u omitido; solo verificamos que no haya campos inesperados
        assertFalse(json.contains("providerSystem"));
        assertFalse(json.contains("\"unexpected\""));
    }
}
