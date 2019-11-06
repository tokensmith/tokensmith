package net.tokensmith.authorization.oauth2.grant.token.exception;

import net.tokensmith.authorization.exception.BaseInformException;

/**
 * Created by tommackenzie on 9/28/16.
 */
public class NotFoundException extends BaseInformException {
    private String error;
    private String description;

    public NotFoundException(String message, String error, String description, int code, Throwable domainCause) {
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
