package org.rootservices.authorization.persistence.exceptions;

/**
 * Created by tommackenzie on 7/15/15.
 */
public class DuplicateRecordException extends Exception {

    public DuplicateRecordException(String message, Throwable cause){
        super(message, cause);
    }
}
