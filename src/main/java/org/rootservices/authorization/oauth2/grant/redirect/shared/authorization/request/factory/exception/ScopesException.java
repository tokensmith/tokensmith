package org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.exception;

import org.rootservices.authorization.constant.ErrorCode;

/**
 * Created by tommackenzie on 2/1/15.
 */
public class ScopesException extends BaseException {

    public ScopesException() {}

    public ScopesException(ErrorCode errorCode, String error, Throwable domainCause) {
        super(errorCode, error, domainCause);
    }
}
