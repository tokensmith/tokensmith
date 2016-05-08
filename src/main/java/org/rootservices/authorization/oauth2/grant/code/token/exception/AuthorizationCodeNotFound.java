package org.rootservices.authorization.oauth2.grant.code.token.exception;

import org.rootservices.authorization.exception.BaseInformException;

/**
 * Created by tommackenzie on 6/6/15.
 */
public class AuthorizationCodeNotFound extends BaseInformException {
    private String error;

    public AuthorizationCodeNotFound(String message, String error, int code) {
        super(message, code);
        this.error = error;
    }

    public AuthorizationCodeNotFound(String message, String error, Throwable domainCause, int code) {
        super(message, domainCause, code);
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
