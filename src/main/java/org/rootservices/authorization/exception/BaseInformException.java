package org.rootservices.authorization.exception;


/**
 * Created by tommackenzie on 2/8/15.
 */
public class BaseInformException extends Exception {

    private int code;

    public BaseInformException(String message, int code) {
        super(message);
        this.code = code;
    }

    public BaseInformException(String message, Throwable cause, int code) {
        super(message, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
