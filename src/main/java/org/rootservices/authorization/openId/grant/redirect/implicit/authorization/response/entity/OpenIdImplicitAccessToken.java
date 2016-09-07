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
    private String accessToken;
    private String idToken;
    private Long expiresIn;
    private String nonce;
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

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
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
