package org.rootservices.authorization.grant.code.protocol.token.factory.exception;

/**
 * Created by tommackenzie on 7/3/15.
 */
public class InvalidValueException extends Exception {
    private String key;
    private String value;

    public InvalidValueException(String message, String key, String value) {
        super(message);
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
