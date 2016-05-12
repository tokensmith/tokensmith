package org.rootservices.authorization.oauth2.grant.redirect.code.token.factory.exception;

import org.rootservices.authorization.exception.BaseInformException;

/**
 * Created by tommackenzie on 7/2/15.
 */
public class DuplicateKeyException extends BaseInformException {
    private String key;

    public DuplicateKeyException(String message, Throwable domainCause, int code, String key) {
        super(message, domainCause, code);
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
