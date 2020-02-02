package net.tokensmith.authorization.openId.grant.redirect.implicit.authorization.response.entity;

import java.net.URI;
import java.util.Optional;

/**
 * Created by tommackenzie on 9/13/16.
 */
public class OpenIdImplicitIdentity {
    private URI redirectUri;
    private String idToken;
    private Optional<String> scope;
    private Optional<String> state;

    // used to let the user update their profile via local token
    private String sessionToken;
    private Long sessionTokenIssuedAt;

    public URI getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(URI redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
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

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public Long getSessionTokenIssuedAt() {
        return sessionTokenIssuedAt;
    }

    public void setSessionTokenIssuedAt(Long sessionTokenIssuedAt) {
        this.sessionTokenIssuedAt = sessionTokenIssuedAt;
    }
}
