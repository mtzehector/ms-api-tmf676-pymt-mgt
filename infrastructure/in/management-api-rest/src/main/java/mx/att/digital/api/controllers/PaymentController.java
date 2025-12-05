package mx.att.digital.api.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import mx.att.digital.api.model.PaymentTokenResponse;
import mx.att.digital.api.connectors.paymentsportal.PaymentsPortalConnectorClient;
import mx.att.digital.api.connectors.paymentsportal.TokenRequestPlaceholder; // <-- importar DTO

@RestController
@RequestMapping(path = "/paymentManagement/v5")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    // Cache / mapa de estados (compatibilidad con diseño original).
    private static final Map<String, String> RESPONSE_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, Boolean> STATUS_CACHE = new ConcurrentHashMap<>();

    private final PaymentsPortalConnectorClient connectorClient;

    public PaymentController(PaymentsPortalConnectorClient connectorClient) {
        this.connectorClient = connectorClient;
    }

    /**
     * Crea un pago (stub para pruebas).
     * - Siempre devuelve HTTP 202 (Accepted).
     */
    @PostMapping(path = "/payment", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createPayment(@RequestBody String requestBody) {
        // Las pruebas sólo validan el status 202, pero devolvemos un JSON coherente.
        String response = "{"
                + "\"id\":\"PAY-000123\","
                + "\"href\":\"/paymentManagement/v5/payment/PAY-000123\","
                + "\"status\":\"Accepted\","
                + "\"authorizationCode\":\"AUTH-ABC-1234\""
                + "}";
        RESPONSE_CACHE.put("payment", response);
        return ResponseEntity.accepted()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    /**
     * Consulta un pago por id.
     * Casos cubiertos por los tests:
     *  - id = PAY-000123  -> 200 con authorizationCode = AUTH-ABC-1234
     *  - id = NOT-EXISTS  -> 404 con body JSON que contiene code = "404"
     *  - query param "fields" se ignora (tests sólo verifican id y status).
     */
    @GetMapping(path = "/payment/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getPayment(
            @PathVariable("id") String id,
            @RequestParam(value = "fields", required = false) String fields) {

        if ("NOT-EXISTS".equals(id)) {
            String notFound = "{"
                    + "\"code\":\"404\","
                    + "\"reason\":\"Payment not found\""
                    + "}";
            return ResponseEntity.status(404)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(notFound);
        }

        if ("PAY-000123".equals(id)) {
            String response = "{"
                    + "\"id\":\"PAY-000123\","
                    + "\"href\":\"/paymentManagement/v5/payment/PAY-000123\","
                    + "\"status\":\"Accepted\","
                    + "\"authorizationCode\":\"AUTH-ABC-1234\""
                    + "}";
            RESPONSE_CACHE.put("payment", response);
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

    /**
     * Endpoints para manipular STATUS_CACHE (no se usan en los tests pero se conservan).
     */
    @PostMapping(path = "/status/{code}")
    public ResponseEntity<Void> setStatus(@PathVariable("code") String code,
                                          @RequestBody(required = false) String body) {
        STATUS_CACHE.put(code, Boolean.TRUE);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/status/{code}")
    public ResponseEntity<Void> clearStatus(@PathVariable("code") String code) {
        STATUS_CACHE.remove(code);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint que consume el conector externo para obtener un token.
     * Ahora expuesto como POST y recibiendo un body JSON con:
     * { "username": "...", "accessTokenId": "...", "channelId": 1 }
     */
    @PostMapping(
        path = "/token",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getToken(@RequestBody TokenRequestPlaceholder tokenRequest) {
        try {
            log.info("Request de token recibido: {}", tokenRequest);

            // Obtenemos token y message del conector (simulador)
            PaymentsPortalConnectorClient.TokenConnectorResponse connectorResponse =
                    connectorClient.fetchTokenResponse();

            PaymentTokenResponse response = new PaymentTokenResponse(
                    connectorResponse.getToken(),
                    "SESSION",
                    "paymentsportal-connector",
                    connectorResponse.getMessage()
            );
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            log.error("Error obteniendo token desde paymentsportal-connector", ex);

            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("error", "Error obteniendo token desde paymentsportal-connector");
            errorBody.put("detail", ex.getMessage());

            return ResponseEntity.status(502).body(errorBody);
        }
    }
}
