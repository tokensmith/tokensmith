package org.rootservices.authorization.oauth2.grant.redirect.code.token.factory.exception;

import org.rootservices.authorization.exception.BaseInformException;

/**
 * Created by tommackenzie on 7/10/15.
 */
public class UnknownKeyException extends BaseInformException {
    private String key;

    public UnknownKeyException(String message, String key, Throwable domainCause, int code) {
        super(message, domainCause, code);
        this.key = key;
    }

    public UnknownKeyException(String message, int code) {
        super(message, code);
    }

    public String getKey() {
        return key;
    }
}
