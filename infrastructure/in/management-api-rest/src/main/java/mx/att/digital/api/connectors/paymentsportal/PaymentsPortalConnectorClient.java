package mx.att.digital.api.connectors.paymentsportal;

import jakarta.annotation.PostConstruct;
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
        TokenRequestPlaceholder request = TokenRequestContext.get();
        
        String username = getValueOrDefault(request, TokenRequestPlaceholder::getUsername, reqUsername);
        String accessTokenId = getValueOrDefault(request, TokenRequestPlaceholder::getAccessTokenId, reqAccessTokenId);
        Integer channelId = getChannelIdOrDefault(request);

        Map<String, Object> body = new HashMap<>();
        body.put("username", username);
        body.put("accessTokenId", accessTokenId);
        body.put("channelId", channelId);
        return body;
    }

    private String getValueOrDefault(TokenRequestPlaceholder request, 
                                     java.util.function.Function<TokenRequestPlaceholder, String> getter, 
                                     String defaultValue) {
        if (request == null) {
            return defaultValue;
        }
        String value = getter.apply(request);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }

    private Integer getChannelIdOrDefault(TokenRequestPlaceholder request) {
        return (request != null && request.getChannelId() != null) 
                ? request.getChannelId() 
                : reqChannelId;
    }

    private TokenConnectorResponse validateAndExtractResponse(ResponseEntity<Map<String, Object>> responseEntity, 
                                                            String requestUrl, 
                                                            Map<String, Object> requestBody) {
        Map<String, Object> map = getResponseBodyOrEmpty(responseEntity);
        
        validateSuccessfulResponse(responseEntity, map, requestUrl, requestBody);
        validateTokenField(map, requestUrl, requestBody);

        String token = extractFieldAsString(map, FIELD_TOKEN);
        String message = extractFieldAsString(map, FIELD_MESSAGE);

        return new TokenConnectorResponse(token, message);
    }

    private Map<String, Object> getResponseBodyOrEmpty(ResponseEntity<Map<String, Object>> responseEntity) {
        Map<String, Object> responseBody = responseEntity.getBody();
        return responseBody != null ? responseBody : Map.of();
    }

    private void validateSuccessfulResponse(ResponseEntity<Map<String, Object>> responseEntity, 
                                           Map<String, Object> map, 
                                           String requestUrl, 
                                           Map<String, Object> requestBody) {
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            String msg = extractFieldAsString(map, FIELD_MESSAGE);
            if (msg == null) {
                msg = "Respuesta de error sin campo 'message'";
            }
            int status = responseEntity.getStatusCode().value();
            String detail = buildErrorDetail(requestUrl, requestBody);
            throw new IllegalStateException(detail + " status=" + status + " message=" + msg);
        }
    }

    private void validateTokenField(Map<String, Object> map, String requestUrl, Map<String, Object> requestBody) {
        if (!map.containsKey(FIELD_TOKEN)) {
            String message = map.containsKey(FIELD_MESSAGE)
                    ? String.valueOf(map.get(FIELD_MESSAGE))
                    : "Connector response missing 'token' field";
            String detail = buildErrorDetail(requestUrl, requestBody);
            throw new IllegalStateException(detail + " message=" + message);
        }
    }

    private String extractFieldAsString(Map<String, Object> map, String fieldName) {
        Object fieldObj = map.get(fieldName);
        return fieldObj == null ? null : fieldObj.toString();
    }

    private String buildErrorDetail(String requestUrl, Map<String, Object> requestBody) {
        return "Error en respuesta de paymentsportal-connector. requestUrl=" + requestUrl
                + ", body=" + requestBody;
    }

    /**
         * DTO interno de respuesta.
         */
        public record TokenConnectorResponse(String token, String message) {
    }
}