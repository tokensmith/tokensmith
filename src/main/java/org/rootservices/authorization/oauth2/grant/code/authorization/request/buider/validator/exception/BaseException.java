package org.rootservices.authorization.oauth2.grant.code.authorization.request.buider.validator.exception;

/**
 * Created by tommackenzie on 2/2/15.
 */
public abstract class BaseException extends Exception {

    public BaseException(String message) {
        super(message);
    }
}
