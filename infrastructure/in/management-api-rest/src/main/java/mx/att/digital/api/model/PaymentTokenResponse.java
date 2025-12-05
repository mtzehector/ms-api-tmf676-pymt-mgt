package mx.att.digital.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentTokenResponse {

    private String token;
    

    @JsonProperty("source")
    private String providerSystem;

    private String message; // <-- nuevo campo opcional

    public PaymentTokenResponse() {
    }

    public PaymentTokenResponse(String token,  String providerSystem) {
        this.token = token;
        this.providerSystem = providerSystem;
    }

    // Nuevo constructor que permite también el message del simulador
    public PaymentTokenResponse(String token, String providerSystem, String message) {
        this.token = token;
        this.providerSystem = providerSystem;
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    

    

    public String getProviderSystem() {
        return providerSystem;
    }

    public void setProviderSystem(String providerSystem) {
        this.providerSystem = providerSystem;
    }

    public String getMessage() { // <-- getter
        return message;
    }

    public void setMessage(String message) { // <-- setter
        this.message = message;
    }
}