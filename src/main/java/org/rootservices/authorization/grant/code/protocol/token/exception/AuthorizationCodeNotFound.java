package org.rootservices.authorization.grant.code.protocol.token.exception;

import org.rootservices.authorization.exception.BaseInformException;

/**
 * Created by tommackenzie on 6/6/15.
 */
public class AuthorizationCodeNotFound extends BaseInformException {

    public AuthorizationCodeNotFound(String message, int code) {
        super(message, code);
    }

    public AuthorizationCodeNotFound(String message, Throwable domainCause, int code) {
        super(message, domainCause, code);
    }
}
