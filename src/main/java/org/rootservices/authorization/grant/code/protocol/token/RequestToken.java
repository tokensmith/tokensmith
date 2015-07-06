package org.rootservices.authorization.grant.code.protocol.token;

import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.grant.code.protocol.token.exception.AuthorizationCodeNotFound;
import org.rootservices.authorization.grant.code.protocol.token.exception.BadRequestException;

/**
 * Created by tommackenzie on 5/24/15.
 */
public interface RequestToken {
    TokenResponse run(TokenInput tokenInput) throws UnauthorizedException, AuthorizationCodeNotFound, BadRequestException;
}
