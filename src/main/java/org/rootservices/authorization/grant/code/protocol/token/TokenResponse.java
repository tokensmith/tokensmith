package org.rootservices.authorization.grant.code.protocol.token;

/**
 * Created by tommackenzie on 6/3/15.
 */
public class TokenResponse {
    private String token;
    private Integer secondsToExpiration;
    private TokenType tokenType;

    public TokenResponse(){}

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getSecondsToExpiration() {
        return secondsToExpiration;
    }

    public void setSecondsToExpiration(Integer secondsToExpiration) {
        this.secondsToExpiration = secondsToExpiration;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }
}
