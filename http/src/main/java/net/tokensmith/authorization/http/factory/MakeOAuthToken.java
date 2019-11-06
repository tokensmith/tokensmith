package net.tokensmith.authorization.http.factory;


import net.tokensmith.authorization.http.factory.exception.TokenException;
import net.tokensmith.authorization.http.response.Token;
import net.tokensmith.authorization.http.response.TokenType;
import net.tokensmith.authorization.oauth2.grant.token.entity.TokenResponse;

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
