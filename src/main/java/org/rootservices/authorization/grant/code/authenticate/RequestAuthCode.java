package org.rootservices.authorization.grant.code.authenticate;

import org.rootservices.authorization.grant.code.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.grant.code.request.AuthRequest;

/**
 * Created by tommackenzie on 4/16/15.
 */
public interface RequestAuthCode {
    String run(String userName, String plainTextPassword, AuthRequest authRequest) throws UnauthorizedException;
    int getSecondsToExpiration();
}
