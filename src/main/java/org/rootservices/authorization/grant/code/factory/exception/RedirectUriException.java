package org.rootservices.authorization.grant.code.factory.exception;

import org.rootservices.authorization.grant.code.constant.ErrorCode;

/**
 * Created by tommackenzie on 2/1/15.
 */
public class RedirectUriException extends BaseException {

    public RedirectUriException() {}

    public RedirectUriException(ErrorCode errorCode, Throwable domainCause) {
        super(errorCode, domainCause);
    }

    public RedirectUriException(ErrorCode errorCode) {
        super(errorCode);
    }


}
