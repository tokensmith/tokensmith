package org.rootservices.authorization.codegrant.factory.exception;

import java.util.UUID;

/**
 * Created by tommackenzie on 2/1/15.
 */
public class ScopesException extends BaseException {
    private UUID clientId;

    public ScopesException(String message, int errorCode, Throwable domainCause) {
        super(message, errorCode, domainCause);
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    public UUID getClientId() {
        return clientId;
    }
}
