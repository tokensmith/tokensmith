package org.rootservices.authorization.oauth2.grant.code.token;

import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.oauth2.grant.code.token.exception.AuthorizationCodeNotFound;
import org.rootservices.authorization.oauth2.grant.code.token.exception.BadRequestException;
import org.rootservices.authorization.oauth2.grant.code.token.exception.CompromisedCodeException;
import org.rootservices.authorization.oauth2.grant.code.token.request.TokenInput;
import org.rootservices.authorization.oauth2.grant.code.token.response.TokenResponse;

/**
 * Created by tommackenzie on 5/24/15.
 */
public interface RequestToken {
    TokenResponse run(TokenInput tokenInput) throws UnauthorizedException, AuthorizationCodeNotFound, BadRequestException, CompromisedCodeException;
}
