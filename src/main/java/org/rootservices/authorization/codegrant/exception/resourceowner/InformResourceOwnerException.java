package org.rootservices.authorization.codegrant.exception.resourceowner;

/**
 * Created by tommackenzie on 11/21/14.
 */
public class InformResourceOwnerException extends Exception {

    private Throwable throwable;

    public InformResourceOwnerException(String message) {
        super(message);
    }

    public InformResourceOwnerException(String message, Throwable throwable) {
        super(message);
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
