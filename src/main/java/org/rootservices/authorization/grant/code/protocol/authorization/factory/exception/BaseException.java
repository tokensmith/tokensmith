package org.rootservices.authorization.grant.code.protocol.authorization.factory.exception;

import org.rootservices.authorization.constant.ErrorCode;

/**
 * Created by tommackenzie on 2/1/15.
 */
public abstract class BaseException extends Exception {

    private int code;
    private String error;
    private Throwable domainCause;

    public BaseException() {}

    public BaseException(String message) {
        super(message);
    }

    public BaseException(ErrorCode errorCode) {
        super(errorCode.getMessage().toString());
        this.code = errorCode.getCode();
        this.error="invalid_request";
    }

    public BaseException(ErrorCode errorCode, Throwable domainCause) {
        super(errorCode.getMessage().toString());
        this.code = errorCode.getCode();
        this.domainCause = domainCause;
        this.error="invalid_request";
    }

    public BaseException(ErrorCode errorCode, String error, Throwable domainCause) {
        super(errorCode.getMessage().toString());
        this.code = errorCode.getCode();
        this.domainCause = domainCause;
        this.error=error;
    }

    public Throwable getDomainCause() {
        return domainCause;
    }

    public int getCode() {
        return code;
    }

    public String getError() {
        return error;
    }

}
