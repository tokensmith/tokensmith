package org.rootservices.authorization.http.factory;


import org.rootservices.authorization.oauth2.grant.token.entity.TokenResponse;
import org.rootservices.authorization.openId.identity.MakeCodeGrantIdentityToken;
import org.rootservices.authorization.openId.identity.exception.*;
import org.rootservices.authorization.http.factory.exception.TokenException;
import org.rootservices.authorization.http.response.OpenIdToken;
import org.rootservices.authorization.http.response.Token;
import org.rootservices.authorization.http.response.TokenType;

/**
 * Created by tommackenzie on 2/20/16.
 */
public class MakeOpenIdToken implements MakeToken {
    private MakeCodeGrantIdentityToken makeCodeGrantIdentityToken;

    public MakeOpenIdToken(MakeCodeGrantIdentityToken makeCodeGrantIdentityToken) {
        this.makeCodeGrantIdentityToken = makeCodeGrantIdentityToken;
    }

    @Override
    public Token make(TokenResponse tokenResponse) throws TokenException {
        String idToken;
        try {
            idToken = makeCodeGrantIdentityToken.make(tokenResponse.getAccessToken(), tokenResponse.getTokenClaims());
        } catch (IdTokenException e) {
            throw new TokenException("Issue creating id token", e);
        } catch (KeyNotFoundException e) {
            throw new TokenException("Key not found while creating id token", e);
        } catch (ResourceOwnerNotFoundException e) {
            throw new TokenException("Resource Owner not found while creating id token", e);
        } catch (ProfileNotFoundException e) {
            throw new TokenException("Profile not found while creating id token", e);
        }

        OpenIdToken token = new OpenIdToken();
        token.setAccessToken(tokenResponse.getAccessToken());
        token.setRefreshToken(tokenResponse.getRefreshAccessToken());
        token.setExpiresIn(tokenResponse.getExpiresIn());
        token.setTokenType(TokenType.BEARER);
        token.setIdToken(idToken);

        return token;
    }
}
