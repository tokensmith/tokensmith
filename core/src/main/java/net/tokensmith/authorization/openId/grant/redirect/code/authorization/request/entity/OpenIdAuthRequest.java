package net.tokensmith.authorization.openId.grant.redirect.code.authorization.request.entity;

import net.tokensmith.authorization.openId.grant.redirect.shared.authorization.request.entity.BaseOpenIdAuthRequest;
import net.tokensmith.parser.Parameter;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class OpenIdAuthRequest extends BaseOpenIdAuthRequest {

    public OpenIdAuthRequest() {}

    public OpenIdAuthRequest(UUID clientId, List<String> responseTypes, URI redirectURI, List<String> scopes, Optional<String> state) {
        this.clientId = clientId;
        this.responseTypes = responseTypes;
        this.redirectURI = redirectURI;
        this.scopes = scopes;
        this.state = state;
    }

    @Parameter(name = "nonce", required = false)
    private Optional<String> nonce;

    public Optional<String> getNonce() {
        return nonce;
    }

    public void setNonce(Optional<String> nonce) {
        this.nonce = nonce;
    }
}
