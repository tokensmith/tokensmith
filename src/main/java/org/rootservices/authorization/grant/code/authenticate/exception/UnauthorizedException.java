package org.rootservices.authorization.grant.code.authenticate.exception;

import org.rootservices.authorization.grant.code.constant.ErrorCode;
import org.rootservices.authorization.grant.code.exception.InformResourceOwnerException;

/**
 * Created by tommackenzie on 4/13/15.
 */
public class UnauthorizedException extends InformResourceOwnerException {

    public UnauthorizedException(String message, Throwable domainCause, int code) {
        super(message, domainCause, code);
    }
}
