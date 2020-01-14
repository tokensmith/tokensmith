package net.toknsmith.login.endpoint.entity.response.openid;

/**
 * OpenId Connect 2 (OIDC2) token. The response from the Identity Server.
 */
public class OpenIdToken extends Token {
    private String idToken;

    public OpenIdToken() {
    }

    public OpenIdToken(String accessToken, String refreshToken, Long expiresIn, TokenType tokenType, String idToken) {
        super(accessToken, refreshToken, expiresIn, tokenType);
        this.idToken = idToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }
}
