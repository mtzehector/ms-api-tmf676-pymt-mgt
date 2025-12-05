package mx.att.digital.api.connectors.paymentsportal;

import jakarta.annotation.PostConstruct;
import mx.att.digital.api.controllers.TokenRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Cliente del conector de Payments Portal.
 *
 * Esta versión:
 *  - Mantiene la firma pública fetchTokenResponse() sin parámetros.
 *  - Lee, si existe, el body de la petición /token desde TokenRequestContext
 *    (username, accessTokenId, channelId).
 *  - Si algún campo NO viene en el body, se usan los valores por defecto
 *    configurados (reqUsername, reqAccessTokenId, reqChannelId).
 *  - Genera logs detallados del request/response hacia el conector.
 */
@Component
public class PaymentsPortalConnectorClient {

    private static final Logger log = LoggerFactory.getLogger(PaymentsPortalConnectorClient.class);
    private static final String FIELD_MESSAGE = "message";
    private static final String FIELD_TOKEN = "token";

    private final RestTemplate restTemplate;

    @Value("${paymentsportal.connector.enabled:true}")
    private boolean enabled;

    @Value("${paymentsportal.connector.base-url:https://coremgmtdev.pre-prod.mx.att.com/paymentsportal-connector}")
    private String baseUrl;

    @Value("${paymentsportal.connector.token-path:/v1/token}")
    private String tokenPath;

    @Value("${paymentsportal.connector.username:admin}")
    private String basicUser;

    @Value("${paymentsportal.connector.password:admin}")
    private String basicPassword;

    @Value("${paymentsportal.connector.req-username:testuser123}")
    private String reqUsername;

    @Value("${paymentsportal.connector.req-access-token-id:token-abc-12345}")
    private String reqAccessTokenId;

    @Value("${paymentsportal.connector.req-channel-id:1}")
    private Integer reqChannelId;

    @Value("${paymentsportal.connector.http-version:1_1}")
    private String httpVersion;

    public PaymentsPortalConnectorClient(@Qualifier("restTemplate676") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void logConfig() {
        log.info("[TMF676] paymentsportal.connector.enabled={}", enabled);
        log.info("[TMF676] paymentsportal.connector.base-url={} token-path={}", baseUrl, tokenPath);
        log.info("[TMF676] paymentsportal.connector.http-version={}", httpVersion);
        log.info("[TMF676] paymentsportal.connector.req-username(default)={} req-channel-id(default)={}",
                reqUsername, reqChannelId);
    }

    /**
     * Método principal llamado por el PaymentController.
     * El body real se toma de TokenRequestContext, si está presente.
     */
    public TokenConnectorResponse fetchTokenResponse() {
        if (!enabled) {
            log.info("[TMF676] paymentsportal-connector deshabilitado por configuración; devolviendo stub-token-disabled");
            return new TokenConnectorResponse("stub-token-disabled", "connector-disabled");
        }

        String requestUrl = baseUrl + tokenPath;
        Map<String, Object> body = buildRequestBody();

        try {
            log.info("[TMF676] Invocando paymentsportal-connector url={} httpVersion={} body={}",
                    requestUrl, httpVersion, body);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
            headers.setBasicAuth(basicUser, basicPassword);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                    requestUrl,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            log.info("[TMF676] Respuesta de paymentsportal-connector status={} body={}", 
                    responseEntity.getStatusCode().value(), responseEntity.getBody());

            return validateAndExtractResponse(responseEntity, requestUrl, body);

        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Error llamando a paymentsportal-connector. requestUrl=" + requestUrl + ", body=" + body, ex);
        }
    }

    private Map<String, Object> buildRequestBody() {
        // Body enviado a /paymentManagement/v5/token, si existe
        TokenRequestPlaceholder request = TokenRequestContext.get();

        String username = (request != null && request.getUsername() != null && !request.getUsername().isEmpty())
                ? request.getUsername()
                : reqUsername;

        String accessTokenId = (request != null && request.getAccessTokenId() != null && !request.getAccessTokenId().isEmpty())
                ? request.getAccessTokenId()
                : reqAccessTokenId;

        Integer channelId = (request != null && request.getChannelId() != null)
                ? request.getChannelId()
                : reqChannelId;

        Map<String, Object> body = new HashMap<>();
        body.put("username", username);
        body.put("accessTokenId", accessTokenId);
        body.put("channelId", channelId);
        return body;
    }

    private TokenConnectorResponse validateAndExtractResponse(ResponseEntity<Map<String, Object>> responseEntity, 
                                                            String requestUrl, 
                                                            Map<String, Object> requestBody) {
        Map<String, Object> responseBody = responseEntity.getBody();
        Map<String, Object> map = responseBody != null ? responseBody : Map.of();
        int status = responseEntity.getStatusCode().value();

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            Object messageObj = map.get(FIELD_MESSAGE);
            String msg = messageObj != null ? messageObj.toString()
                    : "Respuesta de error sin campo 'message'";
            String detail = "Error en respuesta de paymentsportal-connector. requestUrl=" + requestUrl
                    + ", body=" + requestBody;
            throw new IllegalStateException(detail + " status=" + status + " message=" + msg);
        }

        if (!map.containsKey(FIELD_TOKEN)) {
            String message = map.containsKey(FIELD_MESSAGE)
                    ? String.valueOf(map.get(FIELD_MESSAGE))
                    : "Connector response missing 'token' field";
            String detail = "Error en respuesta de paymentsportal-connector. requestUrl=" + requestUrl
                    + ", body=" + requestBody;
            throw new IllegalStateException(detail + " message=" + message);
        }

        Object tokenObj = map.get(FIELD_TOKEN);
        String token = tokenObj == null ? null : tokenObj.toString();

        Object messageObj = map.get(FIELD_MESSAGE);
        String message = messageObj == null ? null : messageObj.toString();

        return new TokenConnectorResponse(token, message);
    }

    /**
     * DTO interno de respuesta.
     */
    public static class TokenConnectorResponse {
        private final String token;
        private final String message;

        public TokenConnectorResponse(String token, String message) {
            this.token = token;
            this.message = message;
        }

        public String getToken() {
            return token;
        }

        public String getMessage() {
            return message;
        }
    }
}