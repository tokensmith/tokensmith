package org.rootservices.authorization.grant.openid.protocol.token.exception;

/**
 * Created by tommackenzie on 3/27/16.
 */
public class ProfileNotFoundException extends Exception{
    public ProfileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
