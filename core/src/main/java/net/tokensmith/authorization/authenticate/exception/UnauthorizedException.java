package net.tokensmith.authorization.authenticate.exception;

import net.tokensmith.authorization.exception.BaseInformException;

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
