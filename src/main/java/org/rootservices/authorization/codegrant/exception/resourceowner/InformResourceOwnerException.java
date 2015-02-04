package org.rootservices.authorization.codegrant.exception.resourceowner;

/**
 * Created by tommackenzie on 11/21/14.
 */
public class InformResourceOwnerException extends Exception {

    private Throwable domainCause;

    public InformResourceOwnerException(String message) {
        super(message);
    }

    public InformResourceOwnerException(String message, Throwable domainCause) {
        super(message);
        this.domainCause = domainCause;
    }

    public Throwable getDomainCause() {
        return domainCause;
    }
}
