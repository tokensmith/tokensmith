package org.rootservices.authorization.openId.grant.redirect.token.authorization.request.factory.exception;

import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.exception.BaseException;
/**
 * Created by tommackenzie on 7/21/16.
 */
public class NonceException extends BaseException {

    public NonceException() {}

    public NonceException(ErrorCode errorCode, Throwable domainCause) {
        super(errorCode, domainCause);
    }
}
