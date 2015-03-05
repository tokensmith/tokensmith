package org.rootservices.authorization.grant.code.request;

import org.rootservices.authorization.grant.code.exception.InformClientException;
import org.rootservices.authorization.grant.code.exception.InformResourceOwnerException;

/**
 * Created by tommackenzie on 11/19/14.
 */
public interface ValidateAuthRequest {
    public boolean run(AuthRequest authRequest) throws InformResourceOwnerException, InformClientException;
}
