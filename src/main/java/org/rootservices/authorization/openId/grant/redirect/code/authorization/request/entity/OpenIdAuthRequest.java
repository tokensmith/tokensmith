package org.rootservices.authorization.openId.grant.redirect.code.authorization.request.entity;

import org.rootservices.authorization.openId.grant.redirect.shared.authorization.request.entity.BaseOpenIdAuthRequest;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 9/30/15.
 */
public class OpenIdAuthRequest extends BaseOpenIdAuthRequest {

    private Optional<String> nonce;

    public OpenIdAuthRequest() {}

    public OpenIdAuthRequest(UUID clientId, List<String> responseTypes, URI redirectURI, List<String> scopes, Optional<String> state) {
        this.clientId = clientId;
        this.responseTypes = responseTypes;
        this.redirectURI = redirectURI;
        this.scopes = scopes;
        this.state = state;
    }

    public Optional<String> getNonce() {
        return nonce;
    }

    public void setNonce(Optional<String> nonce) {
        this.nonce = nonce;
    }
}
