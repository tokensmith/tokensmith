package org.rootservices.authorization.http.factory.exception;

/**
 * Created by tommackenzie on 2/20/16.
 */
public class TokenException extends Exception {
    public TokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
