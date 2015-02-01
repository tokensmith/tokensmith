package org.rootservices.authorization.codegrant.factory.exception;

/**
 * Created by tommackenzie on 2/1/15.
 */
public abstract class BaseException extends Exception {
    private Throwable domainCause;
    private int errorCode;

    public BaseException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public BaseException(String message, int errorCode, Throwable domainCause) {
        super(message);
        this.errorCode = errorCode;
        this.domainCause = domainCause;
    }

    public Throwable getDomainCause() {
        return domainCause;
    }

    public int getErrorCode() {
        return errorCode;
    }

}
