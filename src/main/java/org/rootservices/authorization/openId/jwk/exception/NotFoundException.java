package org.rootservices.authorization.openId.jwk.exception;

/**
 * Created by tommackenzie on 1/4/17.
 */
public class NotFoundException extends Exception {
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
