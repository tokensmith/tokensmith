package org.rootservices.authorization.grant.code.authenticate;

import org.rootservices.authorization.grant.code.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.grant.code.authenticate.input.AuthCodeInput;
import org.rootservices.authorization.grant.code.exception.InformClientException;
import org.rootservices.authorization.grant.code.exception.InformResourceOwnerException;

/**
 * Created by tommackenzie on 4/16/15.
 */
public interface RequestAuthCode {
    String run(AuthCodeInput input) throws UnauthorizedException, InformResourceOwnerException, InformClientException;
}
