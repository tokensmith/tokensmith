package org.rootservices.authorization.grant.code.protocol.authorization.request;

import org.rootservices.authorization.grant.code.exception.InformClientException;
import org.rootservices.authorization.grant.code.exception.InformResourceOwnerException;
import org.rootservices.authorization.grant.code.protocol.authorization.request.entity.AuthRequest;

/**
 * Created by tommackenzie on 11/19/14.
 */
public interface ValidateAuthRequest {
    boolean run(AuthRequest authRequest) throws InformResourceOwnerException, InformClientException;
}
