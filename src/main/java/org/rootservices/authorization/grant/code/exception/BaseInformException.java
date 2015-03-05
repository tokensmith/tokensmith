package org.rootservices.authorization.grant.code.exception;

/**
 * Created by tommackenzie on 2/8/15.
 */
public class BaseInformException extends Exception {

    private Throwable domainCause;
    private int code;

    public BaseInformException(String message, int code) {
        super(message);
        this.code = code;
    }

    public BaseInformException(String message, Throwable domainCause, int code) {
        super(message);
        this.domainCause = domainCause;
        this.code = code;
    }

    public Throwable getDomainCause() {
        return domainCause;
    }

    public int getCode() {
        return code;
    }
}
