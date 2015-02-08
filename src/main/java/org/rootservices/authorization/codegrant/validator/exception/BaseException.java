package org.rootservices.authorization.codegrant.validator.exception;

import org.rootservices.authorization.codegrant.constant.ErrorCode;
import org.rootservices.authorization.codegrant.constant.ValidationError;

/**
 * Created by tommackenzie on 2/2/15.
 */
public abstract class BaseException extends Exception {
    protected int code;

    public BaseException(ErrorCode errorCode) {
        super(errorCode.EMPTY_VALUE.getMessage().toString());
        this.code = errorCode.EMPTY_VALUE.getCode();
    }

    public BaseException(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
