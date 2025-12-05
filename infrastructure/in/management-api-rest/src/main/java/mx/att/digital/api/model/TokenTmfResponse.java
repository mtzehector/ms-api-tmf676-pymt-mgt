package mx.att.digital.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenTmfResponse {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("href")
    private String href;
    
    @JsonProperty("status")
    private String status = "Active";
    
    @JsonProperty("statusDate")
    private String statusDate;
    
    // EXACTA MISMA ESTRUCTURA que paymentMethod
    @JsonProperty("paymentMethod")
    private PaymentMethod paymentMethod;
    
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PaymentMethod {
        @JsonProperty("id")
        private String id;
        
        @JsonProperty("@type")
        private String type = "AccessToken";
        
        @JsonProperty("token")
        private TokenDetail token;
        
        @Data
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class TokenDetail {
            @JsonProperty("externalTokenId")
            private String externalTokenId;
            
            @JsonProperty("provider")
            private String provider = "PaymentsPortal";
        }
    }
}