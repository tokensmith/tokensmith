package org.rootservices.authorization.codegrant.factory.exception;

/**
 * Created by tommackenzie on 2/1/15.
 */
public class ScopesException extends Exception {
    private Throwable domainCause;

    public ScopesException(String message, Throwable domainCause) {
        super(message);
        this.domainCause = domainCause;
    }

    public Throwable getDomainCause(){
        return domainCause;
    }
}
