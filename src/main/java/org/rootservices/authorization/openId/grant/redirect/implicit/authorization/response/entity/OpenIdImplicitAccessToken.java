package org.rootservices.authorization.openId.grant.redirect.implicit.authorization.response.entity;

import org.rootservices.authorization.oauth2.grant.redirect.code.token.response.TokenType;
import org.rootservices.authorization.openId.identity.entity.IdToken;

import java.net.URI;
import java.util.Optional;

/**
 * Created by tommackenzie on 6/23/16.
 */
public class OpenIdImplicitAccessToken {
    private URI redirectUri;
    private Optional<String> accessToken;
    private Optional<TokenType> tokenType;
    private Optional<IdToken> idToken;
    private Optional<Long> expiresIn;
    private Optional<String> scope;
    private Optional<String> state;

    public OpenIdImplicitAccessToken() {
    }

    public URI getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(URI redirectUri) {
        this.redirectUri = redirectUri;
    }


    public Optional<String> getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(Optional<String> accessToken) {
        this.accessToken = accessToken;
    }

    public Optional<TokenType> getTokenType() {
        return tokenType;
    }

    public void setTokenType(Optional<TokenType> tokenType) {
        this.tokenType = tokenType;
    }

    public Optional<IdToken> getIdToken() {
        return idToken;
    }

    public void setIdToken(Optional<IdToken> idToken) {
        this.idToken = idToken;
    }

    public Optional<Long> getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Optional<Long> expiresIn) {
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
