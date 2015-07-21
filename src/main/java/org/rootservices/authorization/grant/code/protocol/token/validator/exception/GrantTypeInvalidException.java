package org.rootservices.authorization.grant.code.protocol.token.validator.exception;

/**
 * Created by tommackenzie on 7/14/15.
 */
public class GrantTypeInvalidException extends InvalidValueException {

    public GrantTypeInvalidException(String message, int code, String key, String value) {
        super(message, code, key, value);
    }
}
