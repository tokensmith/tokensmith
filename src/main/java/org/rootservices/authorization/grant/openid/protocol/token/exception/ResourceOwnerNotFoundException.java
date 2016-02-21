package org.rootservices.authorization.grant.openid.protocol.token.exception;

/**
 * Created by tommackenzie on 1/28/16.
 */
public class ResourceOwnerNotFoundException extends Exception{
    public ResourceOwnerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
