package org.rootservices.authorization.grant.code.exception;

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
