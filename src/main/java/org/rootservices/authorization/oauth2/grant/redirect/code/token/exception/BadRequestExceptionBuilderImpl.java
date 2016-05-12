package org.rootservices.authorization.oauth2.grant.redirect.code.token.exception;

import org.rootservices.authorization.constant.ErrorCode;
import org.springframework.stereotype.Component;

/**
 * Created by tommackenzie on 7/13/15.
 */
@Component
public class BadRequestExceptionBuilderImpl implements BadRequestExceptionBuilder{
    private String message;
    private Throwable domainCause;
    private int code;
    private String error;
    private String description;

    public BadRequestExceptionBuilderImpl() {}

    public BadRequestExceptionBuilderImpl DuplicateKey(String key, int code, Throwable domainCause) {
        this.message = "Bad request";
        this.error = "invalid_request";
        this.description = key + " is repeated";
        this.domainCause = domainCause;
        this.code = code;
        return this;
    }

    public BadRequestExceptionBuilderImpl UnsupportedGrantType(String value, int code, Throwable domainCause) {
        this.message = "Bad request";
        this.error = "unsupported_grant_type";
        this.description = value + " is not supported";
        this.domainCause = domainCause;
        this.code = code;
        return this;
    }

    public BadRequestExceptionBuilderImpl InvalidKeyValue(String key, int code, Throwable domainCause) {
        this.message = "Bad request";
        this.error = "invalid_request";
        this.description = key + " is invalid";
        this.domainCause = domainCause;
        this.code = code;
        return this;
    }

    public BadRequestExceptionBuilderImpl MissingKey(String key, Throwable domainCause) {
        this.message = "Bad request";
        this.error = "invalid_request";
        this.description = key + " is a required field";
        this.domainCause = domainCause;
        this.code = ErrorCode.MISSING_KEY.getCode();
        return this;
    }

    public BadRequestExceptionBuilderImpl UnknownKey(String key, int code, Throwable domainCause) {
        this.message = "Bad request";
        this.error = "invalid_request";
        this.description = key + " is a unknown key";
        this.domainCause = domainCause;
        this.code = code;
        return this;
    }

    public BadRequestExceptionBuilderImpl InvalidPayload(int code, Throwable domainCause) {
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
