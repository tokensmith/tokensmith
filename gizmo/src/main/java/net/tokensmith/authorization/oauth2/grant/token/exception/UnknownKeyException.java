package net.tokensmith.authorization.oauth2.grant.token.exception;

import net.tokensmith.authorization.exception.BaseInformException;

/**
 * Created by tommackenzie on 7/10/15.
 */
public class UnknownKeyException extends BaseInformException {
    private String key;

    public UnknownKeyException(String message, String key, Throwable domainCause, int code) {
        super(message, domainCause, code);
        this.key = key;
    }

    public UnknownKeyException(String message, String key, int code) {
        super(message, code);
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
