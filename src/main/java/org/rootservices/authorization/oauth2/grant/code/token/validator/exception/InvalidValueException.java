package org.rootservices.authorization.oauth2.grant.code.token.validator.exception;

import org.rootservices.authorization.exception.BaseInformException;

/**
 * Created by tommackenzie on 7/3/15.
 */
public class InvalidValueException extends BaseInformException {
    private String key;
    private String value;

    public InvalidValueException(String message, int code, String key, String value) {
        super(message, code);
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
