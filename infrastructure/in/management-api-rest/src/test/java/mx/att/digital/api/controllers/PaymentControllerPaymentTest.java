
package mx.att.digital.api.controllers;

import mx.att.digital.api.connectors.paymentsportal.PaymentsPortalConnectorClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PaymentControllerPaymentTest {

    private MockMvc mvc;

    @BeforeEach
    void setup() {
        PaymentsPortalConnectorClient connector = Mockito.mock(PaymentsPortalConnectorClient.class);
        PaymentController controller = new PaymentController(connector);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("POST /payment devuelve 202 y guarda en caché")
    void createPaymentStoresInCache() throws Exception {
        String body = "{\"any\":\"thing\"}";
        mvc.perform(post("/paymentManagement/v5/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
           .andExpect(status().isAccepted())
           .andExpect(content().string(containsString("PAY-000123")));
    }

    @Test
    @DisplayName("GET /payment/{id} existente devuelve 200 y contenido")
    void getPaymentFound() throws Exception {
        // primero crea para poblar caché
        mvc.perform(post("/paymentManagement/v5/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"a\":1}"))
           .andExpect(status().isAccepted());

        mvc.perform(get("/paymentManagement/v5/payment/PAY-000123").param("fields", "id,href,status"))
           .andExpect(status().isOk())
           .andExpect(content().string(containsString("PAY-000123")));
    }

    @Test
    @DisplayName("GET /payment/{id} inexistente devuelve 404 y code=404")
    void getPaymentNotFound() throws Exception {
        mvc.perform(get("/paymentManagement/v5/payment/NOPE").param("fields", "id"))
           .andExpect(status().isNotFound())
           .andExpect(content().string(containsString("\"code\":\"404\"")));
    }
}
