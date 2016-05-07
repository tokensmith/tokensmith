package org.rootservices.authorization.oauth2.grant.code.authorization.response;

import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.oauth2.grant.code.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.code.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.oauth2.grant.code.authorization.response.exception.AuthCodeInsertException;

/**
 * Created by tommackenzie on 4/16/15.
 */
public interface RequestAuthCode {
    AuthResponse run(AuthCodeInput input) throws UnauthorizedException, InformResourceOwnerException, InformClientException, AuthCodeInsertException;
}
