package org.rootservices.authorization.oauth2.grant.code.token.exception;

import org.rootservices.authorization.exception.BaseInformException;

/**
 * Created by tommackenzie on 7/5/15.
 */
public class BadRequestException extends BaseInformException {
    private String error;
    private String description;

    public BadRequestException(String message, String error, String description, Throwable domainCause, int code) {
        super(message, domainCause, code);
        this.error = error;
        this.description = description;
    }

    public String getError() {
        return error;
    }

    public String getDescription() {
        return description;
    }
}
