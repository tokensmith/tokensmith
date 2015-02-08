package org.rootservices.authorization.codegrant.factory.exception;

import org.rootservices.authorization.codegrant.constant.ErrorCode;

/**
 * Created by tommackenzie on 2/1/15.
 */
public abstract class BaseException extends Exception {

    private int errorCode;
    private String error;
    private Throwable domainCause;

    public BaseException(ErrorCode errorCode) {
        super(errorCode.getMessage().toString());
        this.errorCode=errorCode.getCode();
        this.error="invalid_request";
    }

    public BaseException(ErrorCode errorCode, Throwable domainCause) {
        super(errorCode.getMessage().toString());
        this.errorCode=errorCode.getCode();
        this.domainCause = domainCause;
        this.error="invalid_request";
    }

    public BaseException(ErrorCode errorCode, String error, Throwable domainCause) {
        super(errorCode.getMessage().toString());
        this.errorCode=errorCode.getCode();
        this.domainCause = domainCause;
        this.error=error;
    }

    public Throwable getDomainCause() {
        return domainCause;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getError() {
        return error;
    }

}
