package mx.att.digital.api.connectors.paymentsportal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO para el body requerido por el conector de payments-portal
 * para la obtención de token.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenRequestPlaceholder {

    private String username;
    private String accessTokenId;
    private Integer channelId;

    public TokenRequestPlaceholder() {
        // Default constructor required by Jackson for deserialization
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAccessTokenId() {
        return accessTokenId;
    }

    public void setAccessTokenId(String accessTokenId) {
        this.accessTokenId = accessTokenId;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    @Override
    public String toString() {
        return "TokenRequestPlaceholder{" +
                "username='" + username + '\'' +
                ", accessTokenId='" + (accessTokenId != null ? "***" : "null") + '\'' +
                ", channelId=" + channelId +
                '}';
    }
}
