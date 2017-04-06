package org.rootservices.authorization.openId.grant.redirect.shared.authorization.request.entity;

import org.rootservices.authorization.parse.Parameter;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 8/12/16.
 */
public class BaseOpenIdAuthRequest {
    @Parameter(name = "client_id")
    protected UUID clientId;

    @Parameter(name = "redirect_uri")
    protected URI redirectURI;

    @Parameter(name = "state", required = false)
    protected Optional<String> state = Optional.empty(); // TODO: parser is this right?

    @Parameter(name = "response_type", expected = {"CODE", "TOKEN", "ID_TOKEN"})
    protected List<String> responseTypes;

    @Parameter(name = "scope", required = false)
    protected List<String> scopes;

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
