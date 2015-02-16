package org.rootservices.authorization.codegrant.exception.resourceowner;

/**
 * Created by tommackenzie on 11/25/14.
 */
public class ClientNotFoundException extends InformResourceOwnerException {

    public ClientNotFoundException(String message, Throwable throwable, int code) {
        super(message, throwable, code);
    }
}
