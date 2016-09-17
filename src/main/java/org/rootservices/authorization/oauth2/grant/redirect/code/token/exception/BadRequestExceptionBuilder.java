package org.rootservices.authorization.oauth2.grant.redirect.code.token.exception;

import org.rootservices.authorization.constant.ErrorCode;
import org.springframework.stereotype.Component;

/**
 * Created by tommackenzie on 7/13/15.
 */
@Component
public class BadRequestExceptionBuilder {
    private String message;
    private Throwable domainCause;
    private int code;
    private String error;
    private String description;

    public BadRequestExceptionBuilder() {}

    public BadRequestExceptionBuilder DuplicateKey(String key, int code, Throwable domainCause) {
        this.message = "Bad request";
        this.error = "invalid_request";
        this.description = key + " is repeated";
        this.domainCause = domainCause;
        this.code = code;
        return this;
    }

    public BadRequestExceptionBuilder UnsupportedGrantType(String value, int code, Throwable domainCause) {
        this.message = "Bad request";
        this.error = "unsupported_grant_type";
        this.description = value + " is not supported";
        this.domainCause = domainCause;
        this.code = code;
        return this;
    }

    public BadRequestExceptionBuilder InvalidKeyValue(String key, int code, Throwable domainCause) {
        this.message = "Bad request";
        this.error = "invalid_request";
        this.description = key + " is invalid";
        this.domainCause = domainCause;
        this.code = code;
        return this;
    }

    public BadRequestExceptionBuilder MissingKey(String key, Throwable domainCause) {
        this.message = "Bad request";
        this.error = "invalid_request";
        this.description = key + " is a required field";
        this.domainCause = domainCause;
        this.code = ErrorCode.MISSING_KEY.getCode();
        return this;
    }

    public BadRequestExceptionBuilder UnknownKey(String key, int code, Throwable domainCause) {
        this.message = "Bad request";
        this.error = "invalid_request";
        this.description = key + " is a unknown key";
        this.domainCause = domainCause;
        this.code = code;
        return this;
    }

    public BadRequestExceptionBuilder InvalidPayload(int code, Throwable domainCause) {
        this.message = "Bad request";
        this.error = "invalid_request";
        this.description = "payload is not json";
        this.domainCause = domainCause;
        this.code = code;
        return this;
    }

    public BadRequestException build() {
        return new BadRequestException(message, error, description, domainCause, code);
    }
}
