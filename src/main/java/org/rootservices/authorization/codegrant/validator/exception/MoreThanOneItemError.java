package org.rootservices.authorization.codegrant.validator.exception;

import org.rootservices.authorization.codegrant.constant.ErrorCode;
import org.rootservices.authorization.codegrant.constant.ValidationError;

/**
 * Created by tommackenzie on 1/31/15.
 */
public class MoreThanOneItemError extends BaseException {

    public MoreThanOneItemError(){
        super(ErrorCode.MORE_THAN_ONE_ITEM);
    }
    public MoreThanOneItemError(String message) {
        super(message, ValidationError.MORE_THAN_ONE_ITEM.getCode());
    }
}
