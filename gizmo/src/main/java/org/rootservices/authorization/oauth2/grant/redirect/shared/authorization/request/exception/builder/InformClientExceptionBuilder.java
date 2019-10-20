package org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.builder;

import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Optional;

/**
 * Created by tommackenzie on 11/19/16.
 */
@Scope("prototype")
@Component
public class InformClientExceptionBuilder {
    private String message;
    private String error;
    private String description;
    private Integer errorCode;
    private URI redirectURI;
    private Optional<String> state;
    private Throwable cause;

    public InformClientExceptionBuilder setMessage(String message) {
        this.message = message;
        return this;
    }

    public InformClientExceptionBuilder setError(String error) {
        this.error = error;
        return this;
    }

    public InformClientExceptionBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public InformClientExceptionBuilder setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public InformClientExceptionBuilder setRedirectURI(URI redirectURI) {
        this.redirectURI = redirectURI;
        return this;
    }

    public InformClientExceptionBuilder setState(Optional<String> state) {
        this.state = state;
        return this;
    }

    public InformClientExceptionBuilder setCause(Throwable cause) {
        this.cause = cause;
        return this;
    }

    public InformClientException build() {
        return new InformClientException(
                this.message, this.error, this.description, this.errorCode, this.redirectURI, this.state, this.cause
        );
    }
}
