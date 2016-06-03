package org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.exception;

import org.rootservices.authorization.constant.ErrorCode;

import java.util.UUID;

/**
 * Created by tommackenzie on 2/1/15.
 */
public class ScopesException extends BaseException {

    public ScopesException() {}

    public ScopesException(ErrorCode errorCode, String error, Throwable domainCause) {
        super(errorCode, error, domainCause);
    }
}
