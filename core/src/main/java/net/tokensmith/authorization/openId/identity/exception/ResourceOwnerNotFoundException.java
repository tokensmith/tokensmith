package net.tokensmith.authorization.openId.identity.exception;

/**
 * Created by tommackenzie on 11/27/16.
 */
public class ResourceOwnerNotFoundException extends Exception {
    public ResourceOwnerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
