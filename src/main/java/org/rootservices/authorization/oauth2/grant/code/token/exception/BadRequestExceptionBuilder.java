package org.rootservices.authorization.oauth2.grant.code.token.exception;

/**
 * Created by tommackenzie on 7/13/15.
 */
public interface BadRequestExceptionBuilder {
    BadRequestExceptionBuilderImpl DuplicateKey(String key, int code, Throwable domainCause);
    BadRequestExceptionBuilderImpl UnsupportedGrantType(String value, int code, Throwable domainCause);
    BadRequestExceptionBuilderImpl InvalidKeyValue(String key, int code, Throwable domainCause);
    BadRequestExceptionBuilderImpl MissingKey(String key, Throwable domainCause);
    BadRequestExceptionBuilderImpl UnknownKey(String key, int code, Throwable domainCause);
    BadRequestExceptionBuilderImpl InvalidPayload(int code, Throwable domainCause);
    BadRequestException build();
}
