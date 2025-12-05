package mx.att.digital.api.controllers;

import mx.att.digital.api.connectors.paymentsportal.PaymentsPortalConnectorClient;
import mx.att.digital.api.connectors.paymentsportal.TokenRequestContext;
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
    private static final String JSON_FIELD_ID = "id";
    private static final String JSON_FIELD_HREF = "href";
    private static final String JSON_FIELD_STATUS = "status";
    private static final String JSON_FIELD_AUTH_CODE = "authorizationCode";
    private static final String JSON_FIELD_CODE = "code";
    private static final String JSON_FIELD_REASON = "reason";
    private static final String JSON_FIELD_MESSAGE = "message";
    private static final String STATUS_ACCEPTED = "Accepted";
    private static final String AUTH_CODE_MOCK = "AUTH-ABC-1234";
    private static final String ERROR_CODE_NOT_FOUND = "404";
    private static final String ERROR_REASON_NOT_FOUND = "Payment not found";
    private static final String ERROR_CODE_BAD_GATEWAY = "502";
    private static final String ERROR_REASON_UPSTREAM = "Upstream payment provider error";
    private static final String JSON_SEPARATOR = "\":\"";
    private static final String JSON_QUOTE = "\"";

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
        String sanitizedBody = sanitizeForLog(requestBody);
        log.info("[TMF676] POST /paymentManagement/v5/payment body={}", sanitizedBody);

        String response = "{"
                + JSON_QUOTE + JSON_FIELD_ID + JSON_SEPARATOR + MOCK_PAYMENT_ID + "\","
                + JSON_QUOTE + JSON_FIELD_HREF + "\":\"/paymentManagement/v5/payment/" + MOCK_PAYMENT_ID + "\","
                + JSON_QUOTE + JSON_FIELD_STATUS + JSON_SEPARATOR + STATUS_ACCEPTED + "\","
                + JSON_QUOTE + JSON_FIELD_AUTH_CODE + JSON_SEPARATOR + AUTH_CODE_MOCK + JSON_QUOTE
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

        // Sanitize input to prevent log injection
        String sanitizedId = sanitizeForLog(id);
        String sanitizedFields = sanitizeForLog(fields);
        log.info("[TMF676] GET /paymentManagement/v5/payment/{}?fields={}", sanitizedId, sanitizedFields);

        if (MOCK_PAYMENT_ID.equals(id)) {
            String response = PAYMENT_CACHE.getOrDefault(MOCK_PAYMENT_ID,
                    "{"
                            + JSON_QUOTE + JSON_FIELD_ID + JSON_SEPARATOR + MOCK_PAYMENT_ID + "\","
                            + JSON_QUOTE + JSON_FIELD_HREF + "\":\"/paymentManagement/v5/payment/" + MOCK_PAYMENT_ID + "\","
                            + JSON_QUOTE + JSON_FIELD_STATUS + JSON_SEPARATOR + STATUS_ACCEPTED + "\","
                            + JSON_QUOTE + JSON_FIELD_AUTH_CODE + JSON_SEPARATOR + AUTH_CODE_MOCK + JSON_QUOTE
                            + "}"
            );
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        }

        String notFound = "{"
                + JSON_QUOTE + JSON_FIELD_CODE + JSON_SEPARATOR + ERROR_CODE_NOT_FOUND + "\","
                + JSON_QUOTE + JSON_FIELD_REASON + JSON_SEPARATOR + ERROR_REASON_NOT_FOUND + JSON_QUOTE
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
        String sanitizedRequest = sanitizeForLog(String.valueOf(tokenRequest));
        log.info("[TMF676] POST /paymentManagement/v5/token - requestBody={}", sanitizedRequest);

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
            tokenDetail.setExternalTokenId(connectorResponse.token()); // JWT aquí
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
            errorBody.put(JSON_FIELD_CODE, ERROR_CODE_BAD_GATEWAY);
            errorBody.put(JSON_FIELD_REASON, ERROR_REASON_UPSTREAM);
            errorBody.put(JSON_FIELD_MESSAGE, ex.getMessage());

            return ResponseEntity.status(502).body(errorBody);
        } finally {
            TokenRequestContext.clear();
        }
    }

    /**
     * Sanitizes input string to prevent log injection attacks.
     * Removes newlines and carriage returns that could be used for log forging.
     */
    private String sanitizeForLog(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("[\n\r]", "_");
    }
}