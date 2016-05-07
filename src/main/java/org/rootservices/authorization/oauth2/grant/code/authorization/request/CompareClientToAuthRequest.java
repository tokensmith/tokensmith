package org.rootservices.authorization.oauth2.grant.code.authorization.request;

import org.rootservices.authorization.oauth2.grant.code.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.code.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.oauth2.grant.code.authorization.request.entity.AuthRequest;

/**
 * Created by tommackenzie on 11/19/14.
 */
public interface CompareClientToAuthRequest {
    boolean run(AuthRequest authRequest) throws InformResourceOwnerException, InformClientException;
}
