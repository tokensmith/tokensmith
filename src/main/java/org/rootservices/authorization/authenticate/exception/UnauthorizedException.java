package org.rootservices.authorization.authenticate.exception;

import org.rootservices.authorization.exception.BaseInformException;

/**
 * Created by tommackenzie on 4/13/15.
 */
public class UnauthorizedException extends BaseInformException {

    public UnauthorizedException(String message, int code) {
        super(message, code);
    }

    public UnauthorizedException(String message, Throwable domainCause, int code) {
        super(message, domainCause, code);
    }
}
