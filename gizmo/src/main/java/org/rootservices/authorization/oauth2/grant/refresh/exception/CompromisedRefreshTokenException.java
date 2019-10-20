package org.rootservices.authorization.oauth2.grant.refresh.exception;

/**
 * Created by tommackenzie on 10/10/16.
 */
public class CompromisedRefreshTokenException extends Exception {
    public CompromisedRefreshTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
