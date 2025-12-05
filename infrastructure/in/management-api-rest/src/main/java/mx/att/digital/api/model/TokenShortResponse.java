package mx.att.digital.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Respuesta mínima del endpoint /token.
 *
 * Siempre serializa ambos campos, incluso si message es null:
 * {
 *   "token": "...",
 *   "message": null
 * }
 */
@JsonPropertyOrder({ "token", "message" })
@JsonInclude(JsonInclude.Include.ALWAYS)
public class TokenShortResponse {

    private String token;
    private String message;

    public TokenShortResponse() {
    }

    public TokenShortResponse(String token, String message) {
        this.token = token;
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
