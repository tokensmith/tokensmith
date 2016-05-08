package org.rootservices.authorization.oauth2.grant.code.token.factory.exception;

import org.rootservices.authorization.exception.BaseInformException;

/**
 * Created by tommackenzie on 7/2/15.
 */
public class InvalidPayloadException extends BaseInformException {

    public InvalidPayloadException(String message, Throwable domainCause, int code) {
        super(message, domainCause, code);
    }
}
