package org.rootservices.authorization.oauth2.grant.password;

import org.rootservices.authorization.oauth2.grant.redirect.code.token.MakeBearerToken;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.MakeRefreshToken;
import org.rootservices.authorization.oauth2.grant.token.entity.Extension;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenResponse;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenType;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.repository.*;
import org.rootservices.authorization.security.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by tommackenzie on 9/18/16.
 */
@Component
public class IssueTokenPasswordGrant {
    private RandomString randomString;
    private MakeBearerToken makeBearerToken;
    private TokenRepository tokenRepository;
    private MakeRefreshToken makeRefreshToken;
    private RefreshTokenRepository refreshTokenRepository;
    private ResourceOwnerTokenRepository resourceOwnerTokenRepository;
    private TokenScopeRepository tokenScopeRepository;
    private ClientTokenRepository clientTokenRepository;

    private static String OPENID_SCOPE = "openid";
    private static Long REFRESH_TOKEN_SECONDS_TO_EXPIRATION = 1209600L;

    @Autowired
    public IssueTokenPasswordGrant(RandomString randomString, MakeBearerToken makeBearerToken, TokenRepository tokenRepository, MakeRefreshToken makeRefreshToken, RefreshTokenRepository refreshTokenRepository, ResourceOwnerTokenRepository resourceOwnerTokenRepository, TokenScopeRepository tokenScopeRepository, ClientTokenRepository clientTokenRepository) {
        this.randomString = randomString;
        this.makeBearerToken = makeBearerToken;
        this.tokenRepository = tokenRepository;
        this.makeRefreshToken = makeRefreshToken;
        this.refreshTokenRepository = refreshTokenRepository;
        this.resourceOwnerTokenRepository = resourceOwnerTokenRepository;
        this.tokenScopeRepository = tokenScopeRepository;
        this.clientTokenRepository = clientTokenRepository;
    }

    public TokenResponse run(UUID clientId, UUID resourceOwnerId, String plainTextToken, List<Scope> scopes) {
        Token token = makeBearerToken.run(plainTextToken);
        token.setGrantType(GrantType.PASSWORD);

        try {
            tokenRepository.insert(token);
        } catch( DuplicateRecordException e) {
            // TODO: handle this exception
        }

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
