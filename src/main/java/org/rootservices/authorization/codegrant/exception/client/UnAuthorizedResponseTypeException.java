package org.rootservices.authorization.codegrant.exception.client;

import java.net.URI;

/**
 * Created by tommackenzie on 12/13/14.
 */
public class UnAuthorizedResponseTypeException extends InformClientException {

    public UnAuthorizedResponseTypeException(String message, int code, URI redirectURI) {
        super(message, code, redirectURI);

    }
}
