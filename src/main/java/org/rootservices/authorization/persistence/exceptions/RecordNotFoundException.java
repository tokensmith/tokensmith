package org.rootservices.authorization.persistence.exceptions;

/**
 * Created by tommackenzie on 10/11/14.
 */
public class RecordNotFoundException extends Exception {
    private Throwable domainCause;

    public RecordNotFoundException(String message) {
        super(message);
    }

    public RecordNotFoundException(String message, Throwable domainCause) {
        super(message);
        this.domainCause = domainCause;
    }

    public void setDomainCause(Throwable domainCause) {
        this.domainCause = domainCause;
    }

    public Throwable getDomainCause() {
        return domainCause;
    }
}
