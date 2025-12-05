package mx.att.digital.api.controllers;

import mx.att.digital.api.connectors.paymentsportal.PaymentsPortalConnectorClient;
import mx.att.digital.api.connectors.paymentsportal.TokenRequestPlaceholder;
import mx.att.digital.api.model.TokenTmfResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Controlador TMF676 - Versión Mínima
 */
@RestController
@RequestMapping(path = "/paymentManagement/v5")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    private static final String MOCK_PAYMENT_ID = "PAY-000123";

    // Cache simple de respuestas para pruebas
    private static final Map<String, String> PAYMENT_CACHE = new ConcurrentHashMap<>();

    private final PaymentsPortalConnectorClient connectorClient;

    public PaymentController(PaymentsPortalConnectorClient connectorClient) {
        this.connectorClient = connectorClient;
    }

    // ======================
    //  Endpoints de Payment
    // ======================

    @PostMapping(
            path = "/payment",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createPayment(@RequestBody String requestBody) {
        log.info("[TMF676] POST /paymentManagement/v5/payment body={}", requestBody);

        String response = "{"
                + "\"id\":\"" + MOCK_PAYMENT_ID + "\","
                + "\"href\":\"/paymentManagement/v5/payment/" + MOCK_PAYMENT_ID + "\","
                + "\"status\":\"Accepted\","
                + "\"authorizationCode\":\"AUTH-ABC-1234\""
                + "}";

        PAYMENT_CACHE.put(MOCK_PAYMENT_ID, response);

        return ResponseEntity.accepted()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    @GetMapping(
            path = "/payment/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getPayment(
            @PathVariable("id") String id,
            @RequestParam(value = "fields", required = false) String fields) {

        log.info("[TMF676] GET /paymentManagement/v5/payment/{}?fields={}", id, fields);

        if (MOCK_PAYMENT_ID.equals(id)) {
            String response = PAYMENT_CACHE.getOrDefault(MOCK_PAYMENT_ID,
                    "{"
                            + "\"id\":\"" + MOCK_PAYMENT_ID + "\","
                            + "\"href\":\"/paymentManagement/v5/payment/" + MOCK_PAYMENT_ID + "\","
                            + "\"status\":\"Accepted\","
                            + "\"authorizationCode\":\"AUTH-ABC-1234\""
                            + "}"
            );
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        }

        String notFound = "{"
                + "\"code\":\"404\","
                + "\"reason\":\"Payment not found\""
                + "}";

        return ResponseEntity.status(404)
                .contentType(MediaType.APPLICATION_JSON)
                .body(notFound);
    }

    // ======================
    //  Endpoint de Token (ESTRUCTURA paymentMethod)
    // ======================

    @PostMapping(
            path = "/token",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> getToken(@RequestBody TokenRequestPlaceholder tokenRequest) {
        long start = System.currentTimeMillis();
        log.info("[TMF676] POST /paymentManagement/v5/token - requestBody={}", tokenRequest);

        try {
            TokenRequestContext.set(tokenRequest);

            PaymentsPortalConnectorClient.TokenConnectorResponse connectorResponse =
                    connectorClient.fetchTokenResponse();

            // ESTRUCTURA IDÉNTICA a paymentMethod
            TokenTmfResponse tmfResponse = new TokenTmfResponse();
            String tokenId = "token_" + UUID.randomUUID();
            
            tmfResponse.setId(tokenId);
            tmfResponse.setHref("/paymentManagement/v5/token/" + tokenId);
            tmfResponse.setStatus("Active");
            tmfResponse.setStatusDate(OffsetDateTime.now().toString());
            
            // paymentMethod con misma estructura
            TokenTmfResponse.PaymentMethod paymentMethod = new TokenTmfResponse.PaymentMethod();
            paymentMethod.setId("pm_" + UUID.randomUUID());
            paymentMethod.setType("AccessToken");
            
            // Token anidado igual que en el ejemplo
            TokenTmfResponse.PaymentMethod.TokenDetail tokenDetail = 
                new TokenTmfResponse.PaymentMethod.TokenDetail();
            tokenDetail.setExternalTokenId(connectorResponse.getToken()); // JWT aquí
            tokenDetail.setProvider("PaymentsPortal");
            
            paymentMethod.setToken(tokenDetail);
            tmfResponse.setPaymentMethod(paymentMethod);

            long elapsed = System.currentTimeMillis() - start;
            log.info("[TMF676] Token con estructura paymentMethod generado en {} ms", elapsed);

            return ResponseEntity.ok(tmfResponse);
            
        } catch (Exception ex) {
            long elapsed = System.currentTimeMillis() - start;
            log.error("[TMF676] Error obteniendo token en {} ms", elapsed, ex);

            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("code", "502");
            errorBody.put("reason", "Upstream payment provider error");
            errorBody.put("message", ex.getMessage());

            return ResponseEntity.status(502).body(errorBody);
        } finally {
            TokenRequestContext.clear();
        }
    }
}