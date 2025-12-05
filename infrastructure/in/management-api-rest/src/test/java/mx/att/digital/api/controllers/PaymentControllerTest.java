package mx.att.digital.api.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private mx.att.digital.api.connectors.paymentsportal.PaymentsPortalConnectorClient connectorClient;

    @Test
    void createPayment_returnsAccepted() throws Exception {
        mockMvc.perform(post("/paymentManagement/v5/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isAccepted());
    }

    @Test
    void getPayment_ok() throws Exception {
        mockMvc.perform(get("/paymentManagement/v5/payment/PAY-000123")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorizationCode").value("AUTH-ABC-1234"));
    }

    @Test
    void getPayment_missing_returns404() throws Exception {
        mockMvc.perform(get("/paymentManagement/v5/payment/NOT-EXISTS")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("404"));
    }

    // ... existing code ...

    @Test
    void postToken_returnsTokenResponse() throws Exception {
        // Mock del conector: simulamos que devuelve un token concreto y (opcionalmente) un mensaje
        when(connectorClient.fetchTokenResponse())
                .thenReturn(new mx.att.digital.api.connectors.paymentsportal.PaymentsPortalConnectorClient.TokenConnectorResponse(
                        "token-abc-12345",
                        "Aqui podriamos colocar la URL"
                ));

        String requestJson = """
            {
              "username": "testuser123",
              "accessTokenId": "token-abc-12345",
              "channelId": 1
            }
            """;

        mockMvc.perform(post("/paymentManagement/v5/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentMethod.token.externalTokenId").value("token-abc-12345"))
                .andExpect(jsonPath("$.paymentMethod['@type']").value("AccessToken"))
                .andExpect(jsonPath("$.paymentMethod.token.provider").value("PaymentsPortal"));
            
    }
}
