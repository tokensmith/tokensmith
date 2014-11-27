package org.rootservices.authorization.codegrant.builder;

import org.rootservices.authorization.codegrant.exception.resourceowner.InformResourceOwnerException;
import org.rootservices.authorization.codegrant.request.AuthRequest;

/**
 * Created by tommackenzie on 11/27/14.
 */
public interface AuthRequestBuilder {
    public AuthRequest build(String clientIdentifier, String responseType) throws InformResourceOwnerException;
}
