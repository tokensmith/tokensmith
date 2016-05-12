package org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.exception;

import org.rootservices.authorization.constant.ErrorCode;

/**
 * Created by tommackenzie on 2/1/15.
 */
public class ClientIdException extends BaseException {

    public ClientIdException() {}

    public ClientIdException(ErrorCode errorCode, Throwable domainCause) {
        super(errorCode, domainCause);
    }
}
