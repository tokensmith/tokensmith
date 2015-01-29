package org.rootservices.authorization.codegrant.request;

import org.rootservices.authorization.persistence.entity.ResponseType;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 11/19/14.
 */
public class AuthRequest {
    private UUID clientId;
    private ResponseType responseType;
    private URI redirectURI;

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

    public URI getRedirectURI() {
        return redirectURI;
    }

    public void setRedirectURI(URI redirectURI) {
        this.redirectURI = redirectURI;
    }
}
