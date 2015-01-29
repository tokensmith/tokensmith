package org.rootservices.authorization.codegrant.exception.resourceowner;

/**
 * Created by tommackenzie on 1/28/15.
 */
public class RedirectUriMismatchException extends InformResourceOwnerException {

    public RedirectUriMismatchException(String message) {
        super(message);
    }

    public RedirectUriMismatchException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
