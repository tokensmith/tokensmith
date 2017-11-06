package org.rootservices.authorization.exception;


public class BadRequestException extends BaseInformException {
    private String error; // values like, invalid_request
    private String field; // form input or query param names.
    private String description; // a human readable error description.

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadRequestException(String message, String field, String description) {
        super(message);
        this.field = field;
        this.description = description;
    }

    public BadRequestException(String message, String error, String description, Throwable domainCause, int code) {
        super(message, domainCause, code);
        this.error = error;
        this.description = description;
    }

    public String getField() {
        return field;
    }

    public String getError() {
        return error;
    }

    public String getDescription() {
        return description;
    }
}
