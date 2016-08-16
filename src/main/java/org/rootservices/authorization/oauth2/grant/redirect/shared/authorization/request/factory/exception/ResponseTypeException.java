package org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.exception;

import org.rootservices.authorization.constant.ErrorCode;

/**
 * Created by tommackenzie on 2/1/15.
 */
public class ResponseTypeException extends BaseException {

    public ResponseTypeException() {}
    
    public ResponseTypeException(String message) {
        super(message);
    }

    public ResponseTypeException(ErrorCode errorCode, Throwable domainCause) {
        super(errorCode, domainCause);
    }

    public ResponseTypeException(ErrorCode errorCode, String error, Throwable domainCause) {
        super(errorCode, error, domainCause);
    }

    public ResponseTypeException(ErrorCode errorCode, String error) {
        super(errorCode, error);
    }
}
