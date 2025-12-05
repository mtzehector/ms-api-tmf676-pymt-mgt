package mx.att.digital.api.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AgreementDomainJsonTest {

    @Test
    void roundTripJson_preservesNameAndStatus() throws Exception {
        AgreementDomain src = AgreementDomain.builder()
                .name("Plan Plus")
                .status("ACTIVE")
                .build();
        ObjectMapper om = new ObjectMapper();
        String json = om.writeValueAsString(src);
        AgreementDomain again = om.readValue(json, AgreementDomain.class);
        assertEquals("Plan Plus", again.getName());
        assertEquals("ACTIVE", again.getStatus());
    }
}
