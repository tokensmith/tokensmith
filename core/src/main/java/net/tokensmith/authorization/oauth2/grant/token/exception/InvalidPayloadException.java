package net.tokensmith.authorization.oauth2.grant.token.exception;

import net.tokensmith.authorization.exception.BaseInformException;

/**
 * Created by tommackenzie on 7/2/15.
 */
public class InvalidPayloadException extends BaseInformException {

    public InvalidPayloadException(String message, Throwable domainCause, int code) {
        super(message, domainCause, code);
    }
}
