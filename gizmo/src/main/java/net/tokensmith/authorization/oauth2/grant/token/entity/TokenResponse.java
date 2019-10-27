package net.tokensmith.authorization.oauth2.grant.token.entity;

/**
 * Created by tommackenzie on 6/3/15.
 *
 * Value object
 */
public class TokenResponse {
    private String accessToken;
    private String refreshAccessToken;
    private Long expiresIn;
    private TokenType tokenType;
    private Extension extension;
    private TokenClaims tokenClaims;

    public TokenResponse(){}

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshAccessToken() {
        return refreshAccessToken;
    }

    public void setRefreshAccessToken(String refreshAccessToken) {
        this.refreshAccessToken = refreshAccessToken;
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

    public TokenClaims getTokenClaims() {
        return tokenClaims;
    }

    public void setTokenClaims(TokenClaims tokenClaims) {
        this.tokenClaims = tokenClaims;
    }
}
