package org.rootservices.authorization.codegrant.exception.client;

import org.rootservices.authorization.codegrant.exception.BaseInformException;

import java.net.URI;

/**
 * Created by tommackenzie on 11/21/14.
 */
public class InformClientException extends BaseInformException {

    private URI redirectURI;
    private String error;

    public InformClientException(String message) {
        super(message);
    }

    public InformClientException(String message, String error, URI redirectURI, Throwable domainCause) {
        super(message, domainCause);
        this.error = error;
        this.redirectURI = redirectURI;

    }

    public URI getRedirectURI() {
        return redirectURI;
    }

    public String getError() {
        return error;
    }
}
