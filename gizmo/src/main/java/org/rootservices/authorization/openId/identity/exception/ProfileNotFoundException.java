package org.rootservices.authorization.openId.identity.exception;

/**
 * Created by tommackenzie on 3/27/16.
 */
public class ProfileNotFoundException extends Exception {
    public ProfileNotFoundException(String message) {
        super(message);
    }

    public ProfileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
