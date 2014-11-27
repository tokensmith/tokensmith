package org.rootservices.authorization.codegrant.exception.resourceowner;

/**
 * Created by tommackenzie on 11/27/14.
 */
public class ManyClientIdsException extends InformResourceOwnerException {

    public ManyClientIdsException(String message) {
        super(message);
    }

    public ManyClientIdsException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
