package org.rootservices.authorization.http.controller.exception;

/**
 * Created by tommackenzie on 2/25/17.
 */
public class BadRequestException extends Exception {
    public BadRequestException(String message) {
        super(message);
    }
}
