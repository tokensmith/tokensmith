package org.rootservices.authorization.codegrant.factory.exception;

import org.rootservices.authorization.codegrant.constant.ErrorCode;

import java.util.UUID;

/**
 * Created by tommackenzie on 2/1/15.
 */
public class ResponseTypeException extends BaseException {
    private UUID clientId;

    public ResponseTypeException(ErrorCode errorCode, Throwable domainCause) {
        super(errorCode, domainCause);
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    public UUID getClientId() {
        return clientId;
    }
}
