package org.rootservices.authorization.oauth2.grant.redirect.code.token.response;

/**
 * Created by tommackenzie on 6/3/15.
 *
 * Value object
 */
public class TokenResponse {
    private String accessToken;
    private Long expiresIn;
    private TokenType tokenType;
    private Extension extension;

    public TokenResponse(){}

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    public Extension getExtension() {
        return extension;
    }

    public void setExtension(Extension extension) {
        this.extension = extension;
    }
}
