package org.rootservices.authorization.grant.code.exception;

import org.rootservices.authorization.exception.BaseInformException;

import java.net.URI;

/**
 * Created by tommackenzie on 11/21/14.
 */
public class InformClientException extends BaseInformException {

    private URI redirectURI;
    private String error;

    public InformClientException(String message, String error, int code, URI redirectURI, Throwable domainCause) {
        super(message, domainCause, code);
        this.error = error;
        this.redirectURI = redirectURI;
    }

    public InformClientException(String message, String error, int code, URI redirectURI) {
        super(message, code);
        this.error = error;
        this.redirectURI = redirectURI;
    }

    public InformClientException(String message, int code, URI redirectURI) {
        super(message, code);
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
