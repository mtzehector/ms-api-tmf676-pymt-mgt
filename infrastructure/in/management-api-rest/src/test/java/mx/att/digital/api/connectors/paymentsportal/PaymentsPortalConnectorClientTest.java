
package mx.att.digital.api.connectors.paymentsportal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class PaymentsPortalConnectorClientTest {

    @Mock
    @Qualifier("restTemplate676")
    private RestTemplate restTemplate;

    @InjectMocks
    private PaymentsPortalConnectorClient client;

    @BeforeEach
    void init() throws Exception {
        MockitoAnnotations.openMocks(this);
        setField(client, "baseUrl", "http://localhost:8080/paymentManagement");
        setField(client, "tokenPath", "/v5/token");
        setField(client, "basicUser", "user");
        setField(client, "basicPassword", "pass");
        setField(client, "reqUsername", "defaultUser");
        setField(client, "reqAccessTokenId", "defaultToken");
        setField(client, "reqChannelId", 10);
        setField(client, "enabled", true);
    }

    private static void setField(Object target, String name, Object value) throws Exception {
        var f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    @Test @DisplayName("fetchTokenResponse(): 200 OK retorna token y message")
    void fetchToken_success() {
        Map<String, Object> response = new HashMap<>();
        response.put("token", "abc123");
        response.put("message", "OK");
        var entity = ResponseEntity.ok(response);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
            .thenReturn(entity);

        var result = client.fetchTokenResponse();
        assertNotNull(result);
        assertEquals("abc123", result.token());
        assertEquals("OK", result.message());
    }

    @Test @DisplayName("Utiliza TokenRequestContext si existe")
    void usesTokenRequestContext() {
        TokenRequestPlaceholder req = new TokenRequestPlaceholder();
        req.setUsername("ctxUser");
        req.setAccessTokenId("ctxToken");
        req.setChannelId(42);
        TokenRequestContext.set(req);

        Map<String, Object> response = new HashMap<>();
        response.put("token", "zzz");
        response.put("message", "OK");
        var entity = ResponseEntity.ok(response);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
            .thenReturn(entity);

        var result = client.fetchTokenResponse();
        assertEquals("zzz", result.token());
        TokenRequestContext.clear();
    }

    @Test @DisplayName("Envuelve errores del upstream en IllegalStateException")
    void wrapsError() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
            .thenThrow(new RuntimeException("boom"));

        assertThrows(IllegalStateException.class, () -> client.fetchTokenResponse());
    }

    @Test @DisplayName("Cuando enabled=false, regresa stub-token-disabled")
    void disabledReturnsStub() throws Exception {
        setField(client, "enabled", false);
        var result = client.fetchTokenResponse();
        assertEquals("stub-token-disabled", result.token());
    }

    @Test @DisplayName("Respuesta 500 del conector -> IllegalStateException")
    void non2xxResponseThrows() {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "bad");
        var entity = ResponseEntity.status(500).body(body);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
            .thenReturn(entity);

        assertThrows(IllegalStateException.class, () -> client.fetchTokenResponse());
    }

    @Test @DisplayName("Respuesta 200 sin 'token' -> IllegalStateException")
    void missingTokenThrows() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "OK");
        var entity = ResponseEntity.ok(response);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
            .thenReturn(entity);

        assertThrows(IllegalStateException.class, () -> client.fetchTokenResponse());
    }

    @Test @DisplayName("Respuesta 500 sin 'message' -> IllegalStateException (mensaje por defecto)")
    void non2xxWithoutMessageThrows() {
        Map<String, Object> body = new HashMap<>();
        var entity = ResponseEntity.status(500).body(body);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
            .thenReturn(entity);

        assertThrows(IllegalStateException.class, () -> client.fetchTokenResponse());
    }

    @Test @DisplayName("Headers: Content-Type JSON y BasicAuth presentes")
    void buildsHeadersWithBasicAuth() {
        Map<String, Object> response = new HashMap<>();
        response.put("token", "tok");
        response.put("message", "OK");
        var entity = ResponseEntity.ok(response);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<HttpEntity> captorEntity = ArgumentCaptor.forClass(HttpEntity.class);
        ArgumentCaptor<String> captorUrl = ArgumentCaptor.forClass(String.class);

        when(restTemplate.exchange(captorUrl.capture(), eq(HttpMethod.POST), captorEntity.capture(), any(ParameterizedTypeReference.class)))
            .thenReturn(entity);

        client.fetchTokenResponse();

        HttpEntity<?> sent = captorEntity.getValue();
        assertNotNull(sent);
        HttpHeaders headers = sent.getHeaders();
        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
        String auth = headers.getFirst("Authorization");
        assertNotNull(auth);
        assertTrue(auth.startsWith("Basic "));
        assertTrue(captorUrl.getValue().contains("/v5/token"));
    }
}
