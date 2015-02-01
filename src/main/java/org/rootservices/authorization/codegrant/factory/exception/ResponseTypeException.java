package org.rootservices.authorization.codegrant.factory.exception;

/**
 * Created by tommackenzie on 2/1/15.
 */
public class ResponseTypeException extends BaseException {

    public ResponseTypeException(String message, int errorCode, Throwable domainCause) {
        super(message, errorCode, domainCause);
    }
}
