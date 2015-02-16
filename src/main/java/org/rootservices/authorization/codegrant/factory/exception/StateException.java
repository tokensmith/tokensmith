package org.rootservices.authorization.codegrant.factory.exception;

import org.rootservices.authorization.codegrant.constant.ErrorCode;

/**
 * Created by tommackenzie on 2/1/15.
 */
public class StateException extends BaseException {

    public StateException(ErrorCode errorCode, Throwable domainCause) {
        super(errorCode, domainCause);
    }
}
