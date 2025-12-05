package mx.att.digital.api.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean; // <-- volver a este import
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerExtraTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private mx.att.digital.api.connectors.paymentsportal.PaymentsPortalConnectorClient connectorClient;

    @Test
    void getPayment_withFieldsParam_stillOk() throws Exception {
        mockMvc.perform(post("/paymentManagement/v5/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isAccepted());

        mockMvc.perform(get("/paymentManagement/v5/payment/PAY-000123?fields=id,href,status")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("PAY-000123"))
                .andExpect(jsonPath("$.status").value("Accepted"));
    }
}
