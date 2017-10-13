package org.rootservices.authorization.exception;


public class BadRequestException extends BaseInformException {
    private String error;
    private String description;

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadRequestException(String message, String error, String description, Throwable domainCause, int code) {
        super(message, domainCause, code);
        this.error = error;
        this.description = description;
    }

    public String getError() {
        return error;
    }

    public String getDescription() {
        return description;
    }
}
