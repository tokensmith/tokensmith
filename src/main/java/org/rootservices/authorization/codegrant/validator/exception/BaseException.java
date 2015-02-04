package org.rootservices.authorization.codegrant.validator.exception;

/**
 * Created by tommackenzie on 2/2/15.
 */
public abstract class BaseException extends Exception {
    private int code;

    public BaseException(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
