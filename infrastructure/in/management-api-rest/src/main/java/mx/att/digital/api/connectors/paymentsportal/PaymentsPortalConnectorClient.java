package mx.att.digital.api.connectors.paymentsportal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Cliente del conector de Payments Portal.
 *
 * Usa properties / variables de entorno:
 *  - paymentsportal.connector.enabled
 *  - paymentsportal.connector.base-url
 *  - paymentsportal.connector.token-path
 *  - paymentsportal.connector.username
 *  - paymentsportal.connector.password
 *  - paymentsportal.connector.req-username
 *  - paymentsportal.connector.req-access-token-id
 *  - paymentsportal.connector.req-channel-id
 *  - paymentsportal.connector.http-version (solo informativo ahora)
 */
@Component
public class PaymentsPortalConnectorClient {

    private static final Logger log = LoggerFactory.getLogger(PaymentsPortalConnectorClient.class);

    private final RestTemplate restTemplate;

    public PaymentsPortalConnectorClient(
            @Qualifier("restTemplate676") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${paymentsportal.connector.enabled:true}")
    private boolean enabled;

    // Default apuntando a pre-prod AT&T; se puede sobreescribir por variable de entorno.
    // https://coremgmtdev.pre-prod.mx.att.com/paymentsportal-connector/v1/token
    @Value("${paymentsportal.connector.base-url:https://coremgmtdev.pre-prod.mx.att.com/paymentsportal-connector}")
    private String baseUrl;

    @Value("${paymentsportal.connector.token-path:/v1/token}")
    private String tokenPath;

    // Credenciales Basic Auth; sobre-escribibles vía env.
    @Value("${paymentsportal.connector.username:admin}")
    private String basicUser;

    @Value("${paymentsportal.connector.password:admin}")
    private String basicPassword;

    // Campos que van en el body de la petición al conector
    @Value("${paymentsportal.connector.req-username:testuser123}")
    private String reqUsername;

    @Value("${paymentsportal.connector.req-access-token-id:token-abc-12345}")
    private String reqAccessTokenId;

    @Value("${paymentsportal.connector.req-channel-id:1}")
    private Integer reqChannelId;

    // Conmutador HTTP/1.1 <-> HTTP/2 (solo para logging; el RestTemplate siempre usa el HttpClient del bean)
    @Value("${paymentsportal.connector.http-version:1_1}")
    private String httpVersion; // 1_1 (default), 2, HTTP_2 o h2

    /**
     * DTO interno con la respuesta relevante del conector.
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

    /**
     * Firma usada históricamente por el controlador (sólo token).
     * Se mantiene para compatibilidad, delegando en fetchTokenResponse().
     */
    public String requestToken() {
        TokenConnectorResponse response = fetchTokenResponse();
        return response.getToken();
    }

    /**
     * Obtiene token y mensaje desde el conector externo.
     */
    public TokenConnectorResponse fetchTokenResponse() {
        if (!enabled) {
            // Modo stub cuando el conector está deshabilitado.
            log.info("paymentsportal-connector deshabilitado por configuración; devolviendo stub-token-disabled");
            return new TokenConnectorResponse("stub-token-disabled", null);
        }

        String requestUrl = baseUrl + tokenPath;

        Map<String, Object> body = new HashMap<>();
        body.put("username", reqUsername);
        body.put("accessTokenId", reqAccessTokenId);
        body.put("channelId", reqChannelId);

        try {
            log.info("Invocando paymentsportal-connector url={} httpVersion={} body={}",
                    requestUrl, httpVersion, body);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
            headers.setBasicAuth(basicUser, basicPassword);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> responseEntity =
                    restTemplate.postForEntity(requestUrl, entity, Map.class);

            int status = responseEntity.getStatusCode().value();
            Map<?, ?> map = responseEntity.getBody() != null ? responseEntity.getBody() : Map.of();

            log.info("Respuesta de paymentsportal-connector status={} body={}", status, map);

            // Si el conector responde 4xx/5xx con JSON, lo interpretamos:
            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                Object messageObj = map.get("message");
                String msg = messageObj != null ? messageObj.toString()
                        : "Respuesta de error sin campo 'message'";
                String detail = "Error en respuesta de paymentsportal-connector. requestUrl=" + requestUrl
                        + ", body=" + body;
                throw new IllegalStateException(detail + " status=" + status + " message=" + msg);
            }

            if (!map.containsKey("token")) {
                String message = map.containsKey("message")
                        ? map.get("message").toString()
                        : "Connector response missing 'token' field";
                String detail = "Error en respuesta de paymentsportal-connector. requestUrl=" + requestUrl
                        + ", body=" + body;
                throw new IllegalStateException(detail + " message=" + message);
            }
            Object tokenObj = map.get("token");
            String token = tokenObj == null ? null : tokenObj.toString();

            Object messageObj = map.get("message");
            String message = messageObj == null ? null : messageObj.toString();

            return new TokenConnectorResponse(token, message);
        } catch (Exception ex) {
            log.error("Error invocando paymentsportal-connector. requestUrl={}, body={}", requestUrl, body, ex);
            throw new IllegalStateException(
                    "Error llamando a paymentsportal-connector. requestUrl=" + requestUrl + ", body=" + body, ex);
        }
    }
}
