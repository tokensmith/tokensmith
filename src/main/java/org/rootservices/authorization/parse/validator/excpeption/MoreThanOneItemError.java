package org.rootservices.authorization.parse.validator.excpeption;


public class MoreThanOneItemError extends Exception {
    public MoreThanOneItemError(String message) {
        super(message);
    }
}
