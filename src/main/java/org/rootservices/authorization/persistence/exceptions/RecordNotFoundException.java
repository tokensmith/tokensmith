package org.rootservices.authorization.persistence.exceptions;


/**
 * Created by tommackenzie on 10/11/14.
 */
public class RecordNotFoundException extends Exception {

    public RecordNotFoundException() {}

    public RecordNotFoundException(String message) {
        super(message);
    }

    public RecordNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public Throwable getCause() {
        return super.getCause();
    }
}
