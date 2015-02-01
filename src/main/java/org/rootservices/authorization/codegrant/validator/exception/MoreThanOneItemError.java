package org.rootservices.authorization.codegrant.validator.exception;

/**
 * Created by tommackenzie on 1/31/15.
 */
public class MoreThanOneItemError extends Exception {
    public MoreThanOneItemError(String message) {
        super(message);
    }
}
