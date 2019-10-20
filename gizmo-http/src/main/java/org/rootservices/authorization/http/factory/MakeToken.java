package org.rootservices.authorization.http.factory;


import org.rootservices.authorization.http.factory.exception.TokenException;
import org.rootservices.authorization.http.response.Token;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenResponse;

/**
 * Created by tommackenzie on 2/20/16.
 */
public interface MakeToken {
    Token make(TokenResponse tokenResponse) throws TokenException;
}
