package org.rootservices.authorization.codegrant.exception.client;

import java.net.URI;

/**
 * Created by tommackenzie on 12/13/14.
 */
public class UnAuthorizedResponseTypeException extends InformClientException {

    private URI redirectURI;

    public UnAuthorizedResponseTypeException(String message, URI redirectURI) {
        super(message);
        this.redirectURI = redirectURI;
    }

    public URI getRedirectURI() {
        return redirectURI;
    }
}
