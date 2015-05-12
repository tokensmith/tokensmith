package org.rootservices.authorization.grant.code.factory;

import org.rootservices.authorization.grant.code.exception.InformClientException;
import org.rootservices.authorization.grant.code.exception.InformResourceOwnerException;
import org.rootservices.authorization.grant.code.request.AuthRequest;

import java.util.List;

/**
 * Created by tommackenzie on 2/1/15.
 */
public interface AuthRequestFactory {
    public AuthRequest makeAuthRequest(List<String> clientIds, List<String> responseTypes, List<String> redirectUris, List<String> scopes) throws InformResourceOwnerException, InformClientException;
}