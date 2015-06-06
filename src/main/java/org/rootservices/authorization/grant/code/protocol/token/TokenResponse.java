package org.rootservices.authorization.grant.code.protocol.token;

/**
 * Created by tommackenzie on 6/3/15.
 */
public class TokenResponse {
    private String accessToken;
    private Integer expiresIn;
    private String tokenType;

    public TokenResponse(){}

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
