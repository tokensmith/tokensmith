package org.rootservices.authorization.oauth2.grant.redirect.code.token;

import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.exception.AuthorizationCodeNotFound;
import org.rootservices.authorization.oauth2.grant.foo.exception.BadRequestException;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.exception.CompromisedCodeException;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.request.TokenInput;
import org.rootservices.authorization.oauth2.grant.foo.entity.TokenResponse;

/**
 * Created by tommackenzie on 5/24/15.
 */
public interface RequestToken {
    TokenResponse run(TokenInput tokenInput) throws UnauthorizedException, AuthorizationCodeNotFound, BadRequestException, CompromisedCodeException;
}
