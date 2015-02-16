package org.rootservices.authorization.codegrant.validator.exception;

import org.rootservices.authorization.codegrant.constant.ErrorCode;

/**
 * Created by tommackenzie on 1/18/15.
 */
public class EmptyValueError extends BaseException {

    public EmptyValueError(String message) {
        super(message);
    }
}
