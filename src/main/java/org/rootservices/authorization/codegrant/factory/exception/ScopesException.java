package org.rootservices.authorization.codegrant.factory.exception;

import java.util.UUID;

/**
 * Created by tommackenzie on 2/1/15.
 */
public class ScopesException extends BaseException {
    private UUID clientId;
    private String error;

    public ScopesException(String message, String error, int errorCode, Throwable domainCause) {
        super(message, errorCode, domainCause);
        this.error = error;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    public UUID getClientId() {
        return clientId;
    }

    public String getError(){
        return error;
    }
}
