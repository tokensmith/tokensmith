package org.rootservices.authorization.oauth2.grant.code.authorization.request.exception;

import org.rootservices.authorization.exception.BaseInformException;

/**
 * Created by tommackenzie on 11/21/14.
 */
public class InformResourceOwnerException extends BaseInformException {

    public InformResourceOwnerException(String message, int code) {
        super(message, code);
    }

    public InformResourceOwnerException(String message, Throwable domainCause, int code) {
        super(message, domainCause, code);
    }
}
