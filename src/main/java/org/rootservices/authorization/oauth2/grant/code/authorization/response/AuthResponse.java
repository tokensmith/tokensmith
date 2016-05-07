package org.rootservices.authorization.oauth2.grant.code.authorization.response;

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

    public AuthResponse() {
    }

    public AuthResponse(URI redirectUri, String code, Optional<String> state) {
        this.redirectUri = redirectUri;
        this.code = code;
        this.state = state;
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
}
