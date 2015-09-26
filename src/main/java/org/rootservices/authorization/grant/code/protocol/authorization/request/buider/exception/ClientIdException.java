package org.rootservices.authorization.grant.code.protocol.authorization.request.buider.exception;

import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.exception.BaseException;

/**
 * Created by tommackenzie on 2/1/15.
 */
public class ClientIdException extends BaseException {

    public ClientIdException() {}

    public ClientIdException(ErrorCode errorCode, Throwable domainCause) {
        super(errorCode, domainCause);
    }
}
