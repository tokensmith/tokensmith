package net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.exception;

import net.tokensmith.authorization.exception.BaseInformException;

import java.net.URI;
import java.util.Optional;

/**
 * Created by tommackenzie on 11/21/14.
 */
public class InformClientException extends BaseInformException {

    private String error;
    private String description;
    private URI redirectURI;
    private Optional<String> state;

    public InformClientException(String message, String error, String description, int code, URI redirectURI, Optional<String> state, Throwable domainCause) {
        super(message, domainCause, code);
        this.error = error;
        this.description = description;
        this.redirectURI = redirectURI;
        this.state = state;
    }

    public InformClientException(String message, String error, String description, int code, URI redirectURI, Throwable domainCause) {
        super(message, domainCause, code);
        this.error = error;
        this.description = description;
        this.redirectURI = redirectURI;
    }

    public InformClientException(String message, String error, String description, int code, URI redirectURI, Optional<String> state) {
        super(message, code);
        this.error = error;
        this.description = description;
        this.redirectURI = redirectURI;
        this.state = state;
    }

    public InformClientException(String message, String error, String description, URI redirectURI, Optional<String> state, Throwable cause) {
        super(message, cause);
        this.error = error;
        this.description = description;
        this.redirectURI = redirectURI;
        this.state = state;
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

    public Optional<String> getState() {
        return state;
    }

    public void setState(Optional<String> state) {
        this.state = state;
    }
}
