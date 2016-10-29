package org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.exception;

import org.rootservices.authorization.constant.ErrorCode;

/**
 * Created by tommackenzie on 2/1/15.
 */
public abstract class BaseException extends Exception {

    private int code;
    private String error;
    private String description;

    public BaseException() {}

    public BaseException(String message) {
        super(message);
    }

    public BaseException(ErrorCode errorCode) {
        super(errorCode.getDescription().toString());
        this.code = errorCode.getCode();
        this.error="invalid_request";
    }

    public BaseException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getDescription().toString(), cause);
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
        this.error="invalid_request";
    }

    public BaseException(ErrorCode errorCode, String error) {
        super(errorCode.getDescription());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
        this.error = error;
    }

    public BaseException(ErrorCode errorCode, String error, Throwable cause) {
        super(errorCode.getDescription().toString(), cause);
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
        this.error=error;
    }

    public int getCode() {
        return code;
    }

    public String getError() {
        return error;
    }

    public String getDescription() {
        return description;
    }
}
