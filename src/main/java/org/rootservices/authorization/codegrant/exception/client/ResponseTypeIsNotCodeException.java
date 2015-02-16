package org.rootservices.authorization.codegrant.exception.client;

import java.net.URI;

/**
 * Created by tommackenzie on 11/27/14.
 */
public class ResponseTypeIsNotCodeException extends InformClientException {

    public ResponseTypeIsNotCodeException(String message, int code, URI redirectURI) {
        super(message, code, redirectURI);
    }
}
