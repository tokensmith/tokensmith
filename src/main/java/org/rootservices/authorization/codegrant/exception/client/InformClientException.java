package org.rootservices.authorization.codegrant.exception.client;

import java.net.URI;

/**
 * Created by tommackenzie on 11/21/14.
 */
public class InformClientException extends Exception {

    private Throwable domainCause;
    private URI redirectURI;
    private String error;

    public InformClientException(String message) {
        super(message);
    }

    public InformClientException(String message, String error, URI redirectURI, Throwable domainCause) {
        super(message);
        this.error = error;
        this.redirectURI = redirectURI;
        this.domainCause = domainCause;
    }

    public Throwable getDomainCause() {
        return domainCause;
    }

    public URI getRedirectURI() {
        return redirectURI;
    }

    public String getError() {
        return error;
    }
}
