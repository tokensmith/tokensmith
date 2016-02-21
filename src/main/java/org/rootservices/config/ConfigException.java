package org.rootservices.config;

/**
 * Created by tommackenzie on 1/28/16.
 */
public class ConfigException extends Exception {
    private Throwable domainCause;

    public ConfigException(String message, Throwable domainCause) {
        super(message);
        this.domainCause = domainCause;
    }

    public Throwable getDomainCause() {
        return domainCause;
    }

    public void setDomainCause(Throwable domainCause) {
        this.domainCause = domainCause;
    }
}
