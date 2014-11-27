package org.rootservices.authorization.codegrant.exception.resourceowner;

/**
 * Created by tommackenzie on 11/27/14.
 */
public class MissingClientIdException extends InformResourceOwnerException  {

    public MissingClientIdException(String message) {
        super(message);
    }

    public MissingClientIdException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
