
package mx.att.digital.api.controllers;

import mx.att.digital.api.connectors.paymentsportal.PaymentsPortalConnectorClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PaymentControllerTokenTest {

    @Mock
    private PaymentsPortalConnectorClient connector;

    private MockMvc mvc;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        PaymentController controller = new PaymentController(connector);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("POST /paymentManagement/v5/token devuelve estructura con JWT del conector")
    void tokenHappyPath() throws Exception {
        PaymentsPortalConnectorClient.TokenConnectorResponse resp =
                new PaymentsPortalConnectorClient.TokenConnectorResponse("JWT-123", "OK");
        when(connector.fetchTokenResponse()).thenReturn(resp);

        String body = "{"
                + "\"username\":\"u\","
                + "\"accessTokenId\":\"t\","
                + "\"channelId\":1"
                + "}";

        mvc.perform(post("/paymentManagement/v5/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.paymentMethod.token.externalTokenId", is("JWT-123")));
    }

    @Test
    @DisplayName("POST /paymentManagement/v5/token maneja error y devuelve 502")
    void tokenErrorPath() throws Exception {
        when(connector.fetchTokenResponse()).thenThrow(new IllegalStateException("upstream down"));
        String body = "{\"username\":\"u\"}";
        mvc.perform(post("/paymentManagement/v5/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
           .andExpect(status().isBadGateway())
           .andExpect(jsonPath("$.code", is("502")));
    }
}
