package net.tokensmith.authorization.http.controller.exception;

/**
 * Created by tommackenzie on 2/25/17.
 */
public class BadRequestException extends Exception {
    private String value;

    public BadRequestException(String message, Throwable cause, String value) {
        super(message, cause);
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
