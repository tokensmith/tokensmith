package org.rootservices.authorization.grant.code.protocol.authorization.request.buider.exception;

import org.rootservices.authorization.constant.ErrorCode;

/**
 * Created by tommackenzie on 2/1/15.
 */
public class StateException extends BaseException {

    public StateException() {}

    public StateException(ErrorCode errorCode, Throwable domainCause) {
        super(errorCode, domainCause);
    }
}
