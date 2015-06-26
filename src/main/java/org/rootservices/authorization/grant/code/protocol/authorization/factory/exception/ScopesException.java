package org.rootservices.authorization.grant.code.protocol.authorization.factory.exception;

import org.rootservices.authorization.constant.ErrorCode;

import java.util.UUID;

/**
 * Created by tommackenzie on 2/1/15.
 */
public class ScopesException extends BaseException {
    private UUID clientId;

    public ScopesException() {}

    public ScopesException(ErrorCode errorCode, String error, Throwable domainCause) {
        super(errorCode, error, domainCause);
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    public UUID getClientId() {
        return clientId;
    }
}
