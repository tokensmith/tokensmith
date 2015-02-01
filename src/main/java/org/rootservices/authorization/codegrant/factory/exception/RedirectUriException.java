package org.rootservices.authorization.codegrant.factory.exception;

/**
 * Created by tommackenzie on 2/1/15.
 */
public class RedirectUriException extends BaseException {

    public RedirectUriException(String message, int errorCode) {
        super(message, errorCode);
    }

    public RedirectUriException(String message, int errorCode, Throwable domainCause) {
        super(message, errorCode, domainCause);
    }
}
