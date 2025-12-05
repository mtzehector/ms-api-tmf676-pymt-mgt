
package mx.att.digital.api.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenTmfResponseTest {

    @Test
    void nestedStructureAndDefaults() throws JsonProcessingException {
        TokenTmfResponse tmf = new TokenTmfResponse();
        tmf.setId("id-1");
        tmf.setHref("/paymentManagement/v5/token/id-1");
        tmf.setStatus("Active");
        tmf.setStatusDate("2025-12-01T00:00:00Z");

        TokenTmfResponse.PaymentMethod pm = new TokenTmfResponse.PaymentMethod();
        pm.setId("pm-1");
        pm.setType("AccessToken");

        TokenTmfResponse.PaymentMethod.TokenDetail td = new TokenTmfResponse.PaymentMethod.TokenDetail();
        td.setExternalTokenId("JWT-123");
        pm.setToken(td);
        tmf.setPaymentMethod(pm);

        assertEquals("id-1", tmf.getId());
        assertEquals("pm-1", tmf.getPaymentMethod().getId());
        assertEquals("JWT-123", tmf.getPaymentMethod().getToken().getExternalTokenId());
        assertEquals("PaymentsPortal", tmf.getPaymentMethod().getToken().getProvider());

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        String json = mapper.writeValueAsString(tmf);

        assertTrue(json.contains("\"id\":\"id-1\""));
        assertTrue(json.contains("\"paymentMethod\""));
        assertTrue(json.contains("\"externalTokenId\":\"JWT-123\""));
        assertTrue(json.contains("\"provider\":\"PaymentsPortal\""));
    }

    @Test
    void testLombokMethods() {
        TokenTmfResponse response1 = new TokenTmfResponse();
        response1.setId("123");
        
        TokenTmfResponse response2 = new TokenTmfResponse();
        response2.setId("123");

        TokenTmfResponse response3 = new TokenTmfResponse();
        response3.setId("456");

        // Equals & HashCode
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1, response3);
        
        // ToString
        String s = response1.toString();
        assertNotNull(s);
        assertTrue(s.contains("123"));
    }

    @Test
    void testInnerClassesLombok() {
        // PaymentMethod
        TokenTmfResponse.PaymentMethod pm1 = new TokenTmfResponse.PaymentMethod();
        pm1.setId("pm1");
        TokenTmfResponse.PaymentMethod pm2 = new TokenTmfResponse.PaymentMethod();
        pm2.setId("pm1");
        
        assertEquals(pm1, pm2);
        assertEquals(pm1.hashCode(), pm2.hashCode());
        assertTrue(pm1.toString().contains("pm1"));

        // TokenDetail
        TokenTmfResponse.PaymentMethod.TokenDetail td1 = new TokenTmfResponse.PaymentMethod.TokenDetail();
        td1.setExternalTokenId("t1");
        TokenTmfResponse.PaymentMethod.TokenDetail td2 = new TokenTmfResponse.PaymentMethod.TokenDetail();
        td2.setExternalTokenId("t1");

        assertEquals(td1, td2);
        assertEquals(td1.hashCode(), td2.hashCode());
        assertTrue(td1.toString().contains("t1"));
    }
}
