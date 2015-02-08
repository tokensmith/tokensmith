package org.rootservices.authorization.codegrant.exception.resourceowner;

import org.rootservices.authorization.codegrant.exception.BaseInformException;

/**
 * Created by tommackenzie on 11/21/14.
 */
public class InformResourceOwnerException extends BaseInformException {

    public InformResourceOwnerException(String message) {
        super(message);
    }

    public InformResourceOwnerException(String message, Throwable domainCause) {
        super(message, domainCause);
    }
}
