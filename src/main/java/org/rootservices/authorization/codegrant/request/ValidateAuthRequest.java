package org.rootservices.authorization.codegrant.request;

import org.rootservices.authorization.codegrant.exception.client.InformClientException;
import org.rootservices.authorization.codegrant.exception.resourceowner.InformResourceOwnerException;

/**
 * Created by tommackenzie on 11/19/14.
 */
public interface ValidateAuthRequest {
    public boolean run(AuthRequest authRequest) throws InformResourceOwnerException, InformClientException;
}
