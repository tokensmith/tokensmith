package org.rootservices.authorization.grant.code.authenticate.exception;

import org.rootservices.authorization.grant.code.constant.ErrorCode;
import org.rootservices.authorization.grant.code.exception.BaseInformException;
import org.rootservices.authorization.grant.code.exception.InformResourceOwnerException;

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
