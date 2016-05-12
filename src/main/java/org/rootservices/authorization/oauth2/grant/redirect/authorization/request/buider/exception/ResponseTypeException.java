package org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.exception;

import org.rootservices.authorization.constant.ErrorCode;

import java.util.UUID;

/**
 * Created by tommackenzie on 2/1/15.
 */
public class ResponseTypeException extends BaseException {
    private UUID clientId;

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

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    public UUID getClientId() {
        return clientId;
    }
}
