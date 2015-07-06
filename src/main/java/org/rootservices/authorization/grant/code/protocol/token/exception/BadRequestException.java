package org.rootservices.authorization.grant.code.protocol.token.exception;

import org.rootservices.authorization.exception.BaseInformException;

/**
 * Created by tommackenzie on 7/5/15.
 */
public class BadRequestException extends BaseInformException {
    public BadRequestException(String message, Throwable domainCause, int code) {
        super(message, domainCause, code);
    }
}
