package org.rootservices.authorization.codegrant.request;

import org.rootservices.authorization.codegrant.exception.client.InformClientException;
import org.rootservices.authorization.codegrant.exception.client.ResponseTypeIsNotCodeException;
import org.rootservices.authorization.codegrant.exception.client.UnAuthorizedResponseTypeException;
import org.rootservices.authorization.codegrant.exception.resourceowner.ClientNotFoundException;
import org.rootservices.authorization.codegrant.exception.resourceowner.InformResourceOwnerException;
import org.rootservices.authorization.codegrant.exception.resourceowner.RedirectUriMismatchException;

/**
 * Created by tommackenzie on 11/19/14.
 */
public interface ValidateAuthRequest {
    public boolean run(AuthRequest authRequest) throws ResponseTypeIsNotCodeException, ClientNotFoundException, UnAuthorizedResponseTypeException, RedirectUriMismatchException;
}
