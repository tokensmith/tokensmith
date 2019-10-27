package net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.entity;


import net.tokensmith.authorization.parse.Parameter;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 11/19/14.
 */
public class AuthRequest {
    @Parameter(name = "client_id")
    private UUID clientId;

    @Parameter(name = "redirect_uri", required = false)
    private Optional<URI> redirectURI = Optional.empty();

    @Parameter(name = "state", required = false)
    private Optional<String> state = Optional.empty();

    @Parameter(name = "response_type", expected = {"CODE", "TOKEN", "ID_TOKEN"})
    private List<String> responseTypes;

    @Parameter(name = "scope", required = false)
    private List<String> scopes;

    public AuthRequest() {}

    public AuthRequest(UUID clientId, List<String> responseTypes, Optional<URI> redirectURI, List<String> scopes, Optional<String> state) {
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
