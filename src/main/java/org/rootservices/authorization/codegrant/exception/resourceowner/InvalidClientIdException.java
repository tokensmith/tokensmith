package org.rootservices.authorization.codegrant.exception.resourceowner;

/**
 * Created by tommackenzie on 11/27/14.
 */
public class InvalidClientIdException extends InformResourceOwnerException  {

    public InvalidClientIdException(String message) {
        super(message);
    }

    public InvalidClientIdException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
