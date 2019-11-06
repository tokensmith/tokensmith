package net.tokensmith.authorization.http.factory;


import net.tokensmith.authorization.http.factory.exception.TokenException;
import net.tokensmith.authorization.http.response.Token;
import net.tokensmith.authorization.oauth2.grant.token.entity.TokenResponse;

/**
 * Created by tommackenzie on 2/20/16.
 */
public interface MakeToken {
    Token make(TokenResponse tokenResponse) throws TokenException;
}
