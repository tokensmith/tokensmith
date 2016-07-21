package org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.entity;

import org.rootservices.authorization.persistence.entity.ResponseType;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 11/19/14.
 */
public class AuthRequest {
    private UUID clientId;
    private ResponseType responseType;
    private Optional<URI> redirectURI;
    private List<String> scopes;
    private Optional<String> state;

    public AuthRequest() {}

    public AuthRequest(UUID clientId, ResponseType responseType, Optional<URI> redirectURI, List<String> scopes, Optional<String> state) {
        this.clientId = clientId;
        this.responseType = responseType;
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

    public void setResponseType(ResponseType responseType) {
        this.responseType = responseType;
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public Optional<URI> getRedirectURI() {
        return redirectURI;
    }

    public void setRedirectURI(Optional<URI> redirectURI) {
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
