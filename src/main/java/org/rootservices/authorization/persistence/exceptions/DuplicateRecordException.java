package org.rootservices.authorization.persistence.exceptions;

/**
 * Created by tommackenzie on 7/15/15.
 */
public class DuplicateRecordException extends Exception {
    private Throwable domainCause;

    public DuplicateRecordException(String message, Throwable domainCause){
        super(message);
        this.domainCause = domainCause;
    }

    public Throwable getDomainCause() {
        return domainCause;
    }
}
