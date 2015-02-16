package org.rootservices.authorization.codegrant.validator.exception;

import org.rootservices.authorization.codegrant.constant.ErrorCode;

/**
 * Created by tommackenzie on 2/2/15.
 */
public abstract class BaseException extends Exception {

    public BaseException(String message) {
        super(message);
    }
}
