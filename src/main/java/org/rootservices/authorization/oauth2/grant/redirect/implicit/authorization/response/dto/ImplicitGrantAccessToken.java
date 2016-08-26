package org.rootservices.authorization.oauth2.grant.redirect.implicit.authorization.response.dto;

import org.rootservices.authorization.oauth2.grant.redirect.code.token.response.TokenType;

import java.net.URI;
import java.util.Optional;

/**
 * Created by tommackenzie on 6/23/16.
 */
public class ImplicitGrantAccessToken {
    private URI redirectUri;
    private String accessToken;
    private TokenType tokenType;
    private Long expiresIn;
    private Optional<String> scope;
    private Optional<String> state;

    public ImplicitGrantAccessToken() {
    }

    public ImplicitGrantAccessToken(URI redirectUri, String accessToken, TokenType tokenType, Long expiresIn, Optional<String> scope, Optional<String> state) {
        this.redirectUri = redirectUri;
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.scope = scope;
        this.state = state;
    }

    public URI getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(URI redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public Optional<String> getScope() {
        return scope;
    }

    public void setScope(Optional<String> scope) {
        this.scope = scope;
    }

    public Optional<String> getState() {
        return state;
    }

    public void setState(Optional<String> state) {
        this.state = state;
    }
}
