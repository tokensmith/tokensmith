package org.rootservices.authorization.grant.code.protocol.token;

import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.exception.BaseInformException;
import org.rootservices.authorization.grant.code.protocol.token.exception.AuthorizationCodeNotFound;

/**
 * Created by tommackenzie on 5/24/15.
 */
public interface RequestToken {
    TokenResponse run(TokenRequest tokenRequest) throws UnauthorizedException, AuthorizationCodeNotFound;
}