package org.rootservices.authorization.oauth2.grant.refresh;

import org.rootservices.authorization.oauth2.grant.refresh.exception.CompromisedRefreshTokenException;
import org.rootservices.authorization.oauth2.grant.token.MakeBearerToken;
import org.rootservices.authorization.oauth2.grant.token.MakeRefreshToken;
import org.rootservices.authorization.oauth2.grant.token.builder.TokenResponseBuilder;
import org.rootservices.authorization.oauth2.grant.token.entity.Extension;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenResponse;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenType;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.repository.*;
import org.rootservices.authorization.security.RandomString;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.ArrayList;
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
    private TokenChainRepository tokenChainRepository;
    private MakeRefreshToken makeRefreshToken;
    private RefreshTokenRepository refreshTokenRepository;
    private ResourceOwnerTokenRepository resourceOwnerTokenRepository;
    private TokenScopeRepository tokenScopeRepository;
    private ClientTokenRepository clientTokenRepository;
    private TokenResponseBuilder tokenResponseBuilder;

    private String issuer;
    private static String COMPROMISED_MESSAGE = "refresh token was already used";
    private static String OPENID_SCOPE = "openid";

    public IssueTokenRefreshGrant(RandomString randomString, MakeBearerToken makeBearerToken, TokenRepository tokenRepository, TokenChainRepository tokenChainRepository, MakeRefreshToken makeRefreshToken, RefreshTokenRepository refreshTokenRepository, ResourceOwnerTokenRepository resourceOwnerTokenRepository, TokenScopeRepository tokenScopeRepository, ClientTokenRepository clientTokenRepository, TokenResponseBuilder tokenResponseBuilder, String issuer) {
        this.randomString = randomString;
        this.makeBearerToken = makeBearerToken;
        this.tokenRepository = tokenRepository;
        this.tokenChainRepository = tokenChainRepository;
        this.makeRefreshToken = makeRefreshToken;
        this.refreshTokenRepository = refreshTokenRepository;
        this.resourceOwnerTokenRepository = resourceOwnerTokenRepository;
        this.tokenScopeRepository = tokenScopeRepository;
        this.clientTokenRepository = clientTokenRepository;
        this.tokenResponseBuilder = tokenResponseBuilder;
        this.issuer = issuer;
    }

    public TokenResponse run(UUID clientId, UUID resourceOwnerId, UUID previousTokenId, UUID refreshTokenId, Token headToken, List<Scope> scopes) throws CompromisedRefreshTokenException {
        String accessToken = randomString.run();
        Token token = makeBearerToken.run(accessToken);
        token.setGrantType(GrantType.REFRESSH);

        try {
            tokenRepository.insert(token);
        } catch( DuplicateRecordException e) {
            // TODO: handle this exception
        }

        TokenChain tokenChain = makeTokenChain(token, previousTokenId, refreshTokenId);
        try {
            tokenChainRepository.insert(tokenChain);
        } catch (DuplicateRecordException e) {
            throw new CompromisedRefreshTokenException(COMPROMISED_MESSAGE, e);
        }

        String refreshAccessToken = randomString.run();
        RefreshToken refreshToken = makeRefreshToken.run(token, headToken, refreshAccessToken);
        try {
            refreshTokenRepository.insert(refreshToken);
        } catch (DuplicateRecordException e) {
            // TODO: handle this exception
        }

        ResourceOwnerToken resourceOwnerToken = makeResourceOwnerToken(resourceOwnerId, token);
        resourceOwnerTokenRepository.insert(resourceOwnerToken);

        ClientToken clientToken = makeClientToken(clientId, token.getId());
        clientTokenRepository.insert(clientToken);

        Extension extension = Extension.NONE;
        for(Scope scope: scopes) {
            TokenScope ts = new TokenScope();
            ts.setId(UUID.randomUUID());
            ts.setTokenId(token.getId());
            ts.setScope(scope);

            if (OPENID_SCOPE.equalsIgnoreCase(ts.getScope().getName())) {
                extension = Extension.IDENTITY;
            }

            tokenScopeRepository.insert(ts);
        }

        // build the response.
        List<String> audience = new ArrayList<>();
        audience.add(clientId.toString());

        TokenResponse tr = tokenResponseBuilder
                .setAccessToken(accessToken)
                .setRefreshAccessToken(refreshAccessToken)
                .setTokenType(TokenType.BEARER)
                .setExpiresIn(makeBearerToken.getSecondsToExpiration())
                .setExtension(extension)
                .setIssuer(issuer)
                .setAudience(audience)
                .setIssuedAt(OffsetDateTime.now().toEpochSecond())
                .setExpirationTime(token.getExpiresAt().toEpochSecond())
                .setAuthTime(headToken.getCreatedAt().toEpochSecond())
                .build();
        return tr;
    }

    protected TokenChain makeTokenChain(Token token, UUID previousTokenId, UUID refreshTokenId) {
        TokenChain tokenChain = new TokenChain();
        tokenChain.setId(UUID.randomUUID());
        tokenChain.setToken(token);

        Token previousToken = new Token();
        previousToken.setId(previousTokenId);
        tokenChain.setPreviousToken(previousToken);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(refreshTokenId);
        tokenChain.setRefreshToken(refreshToken);

        return tokenChain;
    }

    protected ResourceOwnerToken makeResourceOwnerToken(UUID resourceOwnerId, Token token) {
        ResourceOwner resourceOwner = new ResourceOwner();
        resourceOwner.setId(resourceOwnerId);
        ResourceOwnerToken resourceOwnerToken = new ResourceOwnerToken();
        resourceOwnerToken.setId(UUID.randomUUID());
        resourceOwnerToken.setResourceOwner(resourceOwner);
        resourceOwnerToken.setToken(token);

        return resourceOwnerToken;
    }

    protected ClientToken makeClientToken(UUID clientId, UUID tokenId) {
        ClientToken clientToken = new ClientToken();
        clientToken.setId(UUID.randomUUID());
        clientToken.setClientId(clientId);
        clientToken.setTokenId(tokenId);

        return clientToken;
    }
}
