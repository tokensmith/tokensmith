package org.rootservices.authorization.codegrant.validator.exception;

import org.rootservices.authorization.codegrant.constant.ErrorCode;

/**
 * Created by tommackenzie on 1/31/15.
 */
public class MoreThanOneItemError extends BaseException {


    public MoreThanOneItemError(String message) {
        super(message);
    }
}
