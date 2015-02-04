package org.rootservices.authorization.codegrant.validator.exception;

import org.rootservices.authorization.codegrant.constant.ValidationError;

/**
 * Created by tommackenzie on 1/31/15.
 */
public class ParamIsNullError extends BaseException {
    public ParamIsNullError(String message) {
        super(message, ValidationError.NULL.getCode());
    }
}
