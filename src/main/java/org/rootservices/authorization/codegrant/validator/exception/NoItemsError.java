package org.rootservices.authorization.codegrant.validator.exception;

import org.rootservices.authorization.codegrant.constant.ValidationError;

/**
 * Created by tommackenzie on 1/31/15.
 */
public class NoItemsError extends BaseException {
    public NoItemsError(String message) {
        super(message, ValidationError.EMPTY_LIST.getCode());
    }
}
