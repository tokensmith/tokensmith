package org.rootservices.authorization.http.factory;


import org.rootservices.authorization.http.factory.exception.TokenException;
import org.rootservices.authorization.http.response.Token;
import org.rootservices.authorization.http.response.TokenType;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenResponse;

/**
 * Created by tommackenzie on 2/20/16.
 */
public class MakeOAuthToken implements MakeToken {
    @Override
    public Token make(TokenResponse tokenResponse) throws TokenException {
        Token token = new Token();
        token.setAccessToken(tokenResponse.getAccessToken());
        token.setRefreshToken(tokenResponse.getRefreshAccessToken());
        token.setExpiresIn(tokenResponse.getExpiresIn());
        token.setTokenType(TokenType.BEARER);

        return token;
    }
}
