package org.rootservices.authorization.exception;



public class BaseInformException extends Exception {

    private int code;

    public BaseInformException(String message) {
        super(message);
    }

    public BaseInformException(String message, int code) {
        super(message);
        this.code = code;
    }

    public BaseInformException(String message, Throwable cause, int code) {
        super(message, cause);
        this.code = code;
    }

    public BaseInformException(String message, Throwable cause) {
        super(message, cause);
    }

    public int getCode() {
        return code;
    }
}
