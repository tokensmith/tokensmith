package org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception;

import org.rootservices.authorization.exception.BaseInformException;

import java.net.URI;

/**
 * Created by tommackenzie on 11/21/14.
 */
public class InformClientException extends BaseInformException {

    private URI redirectURI;
    private String error;
    private String description;

    public InformClientException(String message, String error, String description, int code, URI redirectURI, Throwable domainCause) {
        super(message, domainCause, code);
        this.error = error;
        this.description = description;
        this.redirectURI = redirectURI;
    }

    public InformClientException(String message, String error, String description, int code, URI redirectURI) {
        super(message, code);
        this.error = error;
        this.description = description;
        this.redirectURI = redirectURI;
    }

    public InformClientException(String message, int code, URI redirectURI) {
        super(message, code);
        this.redirectURI = redirectURI;
    }

    public URI getRedirectURI() {
        return redirectURI;
    }

    public String getError() {
        return error;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
