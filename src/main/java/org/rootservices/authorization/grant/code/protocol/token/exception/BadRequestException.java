package org.rootservices.authorization.grant.code.protocol.token.exception;

import org.rootservices.authorization.exception.BaseInformException;

/**
 * Created by tommackenzie on 7/5/15.
 */
public class BadRequestException extends BaseInformException {
    private String error;
    private String description;

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

    public static class Builder {
        private String message;
        private Throwable domainCause;
        private int code;
        private String error;
        private String description;

        public Builder() {}

        public Builder DuplicateKey(String key, int code, Throwable domainCause) {
            this.message = "Bad request";
            this.error = "invalid_request";
            this.description = key + " is repeated";
            this.domainCause = domainCause;
            this.code = code;
            return this;
        }

        public BadRequestException build() {
            return new BadRequestException(message, error, description, domainCause, code);
        }
    }
}
