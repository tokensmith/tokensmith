package org.rootservices.authorization.codegrant.factory;

import org.rootservices.authorization.codegrant.factory.exception.ClientIdException;
import org.rootservices.authorization.codegrant.factory.exception.RedirectUriException;
import org.rootservices.authorization.codegrant.factory.exception.ResponseTypeException;
import org.rootservices.authorization.codegrant.factory.exception.ScopesException;
import org.rootservices.authorization.codegrant.request.AuthRequest;

import java.util.List;

/**
 * Created by tommackenzie on 2/1/15.
 */
public interface AuthRequestFactory {
    public AuthRequest makeAuthRequest(List<String> clientIds, List<String> responseTypes, List<String> redirectUris, List<String> scopes) throws ClientIdException, ResponseTypeException, RedirectUriException, ScopesException;
}
