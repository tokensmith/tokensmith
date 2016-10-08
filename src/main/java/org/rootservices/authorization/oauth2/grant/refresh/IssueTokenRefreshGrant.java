package org.rootservices.authorization.oauth2.grant.refresh;

import org.rootservices.authorization.oauth2.grant.token.MakeBearerToken;
import org.rootservices.authorization.oauth2.grant.token.MakeRefreshToken;
import org.rootservices.authorization.oauth2.grant.token.entity.Extension;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenResponse;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenType;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.repository.*;
import org.rootservices.authorization.security.RandomString;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Created by tommackenzie on 10/7/16.
 */
@Component
public class IssueTokenRefreshGrant {
    private RandomString randomString;
    private MakeBearerToken makeBearerToken;
    private TokenRepository tokenRepository;
    private MakeRefreshToken makeRefreshToken;
    private RefreshTokenRepository refreshTokenRepository;
    private ResourceOwnerTokenRepository resourceOwnerTokenRepository;
    private TokenScopeRepository tokenScopeRepository;
    private ClientTokenRepository clientTokenRepository;

    private static String OPENID_SCOPE = "openid";

    public TokenResponse run(UUID clientId, UUID resourceOwnerId, UUID refreshTokenId, String plainTextToken, List<Scope> scopes) {
        Token token = makeBearerToken.run(plainTextToken);
        token.setGrantType(GrantType.REFRESSH);

        try {
            tokenRepository.insert(token);
        } catch( DuplicateRecordException e) {
            // TODO: handle this exception
        }

        // TODO: mark refresh token as used.

        String refreshAccessToken = randomString.run();
        RefreshToken refreshToken = makeRefreshToken.run(token.getId(), refreshAccessToken);

        try {
            refreshTokenRepository.insert(refreshToken);
        } catch (DuplicateRecordException e) {
            // TODO: handle this exception
        }

        ResourceOwner resourceOwner = new ResourceOwner();
        resourceOwner.setId(resourceOwnerId);
        ResourceOwnerToken resourceOwnerToken = new ResourceOwnerToken();
        resourceOwnerToken.setId(UUID.randomUUID());
        resourceOwnerToken.setResourceOwner(resourceOwner);
        resourceOwnerToken.setToken(token);

        resourceOwnerTokenRepository.insert(resourceOwnerToken);

        ClientToken clientToken = new ClientToken();
        clientToken.setId(UUID.randomUUID());
        clientToken.setClientId(clientId);
        clientToken.setTokenId(token.getId());

        clientTokenRepository.insert(clientToken);

        Boolean isOpenId = false;
        for(Scope scope: scopes) {
            TokenScope ts = new TokenScope();
            ts.setId(UUID.randomUUID());
            ts.setTokenId(token.getId());
            ts.setScope(scope);

            if (OPENID_SCOPE.equalsIgnoreCase(ts.getScope().getName())) {
                isOpenId = true;
            }

            tokenScopeRepository.insert(ts);
        }

        // TODO: get original auth time.
        TokenResponse tr = makeTokenResponse(plainTextToken, refreshAccessToken, makeBearerToken.getSecondsToExpiration(), isOpenId);
        return tr;
    }

    protected TokenResponse makeTokenResponse(String accessToken, String refreshAccessToken, Long secondsToExpiration, boolean isOpenId) {
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(accessToken);
        tokenResponse.setRefreshAccessToken(refreshAccessToken);
        tokenResponse.setExpiresIn(secondsToExpiration);
        tokenResponse.setTokenType(TokenType.BEARER);

        Extension extension = Extension.NONE;
        if (isOpenId) {
            extension = Extension.IDENTITY;
        }
        tokenResponse.setExtension(extension);

        return tokenResponse;
    }
}
