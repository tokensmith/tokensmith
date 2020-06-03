package net.tokensmith.authorization.oauth2.grant.redirect.code.authorization.response;

import java.net.URI;
import java.util.Optional;

/**
 * Created by tommackenzie on 4/29/15.
 *
 * Section 4.1.2
 */
public class AuthResponse {
    private URI redirectUri;
    private String code;
    private Optional<String> state;
    // used to let the user update their profile via local token
    private String sessionToken;
    private Long sessionTokenIssuedAt;

    public AuthResponse() {
    }

    public AuthResponse(URI redirectUri, String code, Optional<String> state, String sessionToken, Long sessionTokenIssuedAt) {
        this.redirectUri = redirectUri;
        this.code = code;
        this.state = state;
        this.sessionToken = sessionToken;
        this.sessionTokenIssuedAt = sessionTokenIssuedAt;
    }

    public URI getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(URI redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
