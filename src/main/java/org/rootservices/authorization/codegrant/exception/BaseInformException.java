package org.rootservices.authorization.codegrant.exception;

/**
 * Created by tommackenzie on 2/8/15.
 */
public class BaseInformException extends Exception {

    private Throwable domainCause;

    public BaseInformException(String message) {
        super(message);
    }

    public BaseInformException(String message, Throwable domainCause) {
        super(message);
        this.domainCause = domainCause;
    }

    public Throwable getDomainCause() {
        return domainCause;
    }
}
