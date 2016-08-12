package org.rootservices.authorization.openId.grant.redirect.shared.authorization.request.entity;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 9/30/15.
 */
public class OpenIdAuthRequest {
    private UUID clientId;
    private List<String> responseTypes;
    private URI redirectURI;
    private List<String> scopes;
    private Optional<String> state;

    public OpenIdAuthRequest() {}

    public OpenIdAuthRequest(UUID clientId, List<String> responseTypes, URI redirectURI, List<String> scopes, Optional<String> state) {
        this.clientId = clientId;
        this.responseTypes = responseTypes;
        this.redirectURI = redirectURI;
        this.scopes = scopes;
        this.state = state;
    }

    public UUID getClientId() {
        return clientId;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    public List<String> getResponseTypes() {
        return responseTypes;
    }

    public void setResponseTypes(List<String> responseTypes) {
        this.responseTypes = responseTypes;
    }

    public URI getRedirectURI() {
        return redirectURI;
    }

    public void setRedirectURI(URI redirectURI) {
        this.redirectURI = redirectURI;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }

    public Optional<String> getState() {
        return state;
    }

    public void setState(Optional<String> state) {
        this.state = state;
    }
}
