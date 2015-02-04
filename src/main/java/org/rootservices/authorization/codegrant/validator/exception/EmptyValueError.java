package org.rootservices.authorization.codegrant.validator.exception;

import org.rootservices.authorization.codegrant.constant.ValidationError;

/**
 * Created by tommackenzie on 1/18/15.
 */
public class EmptyValueError extends BaseException {

    public EmptyValueError(String message) {
        super(message, ValidationError.EMPTY_VALUE.getCode());
    }
}
