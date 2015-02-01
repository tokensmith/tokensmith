package org.rootservices.authorization.codegrant.factory.exception;

/**
 * Created by tommackenzie on 2/1/15.
 */
public class ClientIdException extends BaseException {

    public ClientIdException(String message, int errorCode, Throwable domainCause) {
        super(message, errorCode, domainCause);
    }
}
