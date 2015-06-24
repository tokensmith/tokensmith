package org.rootservices.authorization.grant.code.protocol.authorization.response;

import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.grant.code.exception.InformClientException;
import org.rootservices.authorization.grant.code.exception.InformResourceOwnerException;

/**
 * Created by tommackenzie on 4/16/15.
 */
public interface RequestAuthCode {
    AuthResponse run(AuthCodeInput input) throws UnauthorizedException, InformResourceOwnerException, InformClientException;
}
