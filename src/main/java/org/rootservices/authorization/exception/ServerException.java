package org.rootservices.authorization.exception;

/**
 * Created by tommackenzie on 11/5/16.
 */
public class ServerException extends BaseInformException {
    public ServerException(String message, Throwable cause, int code) {
        super(message, cause, code);
    }
}
