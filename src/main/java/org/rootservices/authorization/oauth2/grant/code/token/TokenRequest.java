package org.rootservices.authorization.oauth2.grant.code.token;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 6/26/15.
 *
 */
public class TokenRequest {
    private String grantType;
    private String code;
    private Optional<URI> redirectUri = Optional.empty();

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Optional<URI> getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(Optional<URI> redirectUri) {
        this.redirectUri = redirectUri;
    }
}
