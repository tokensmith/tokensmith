package org.rootservices.authorization.oauth2.grant.refresh;

import org.rootservices.authorization.exception.ServerException;
import org.rootservices.authorization.oauth2.grant.refresh.exception.CompromisedRefreshTokenException;
import org.rootservices.authorization.oauth2.grant.token.builder.TokenResponseBuilder;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenGraph;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenResponse;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenType;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
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
    private InsertTokenGraphRefreshGrant insertTokenGraphRefreshGrant;
    private TokenChainRepository tokenChainRepository;
    private ResourceOwnerTokenRepository resourceOwnerTokenRepository;
    private TokenResponseBuilder tokenResponseBuilder;

    private String issuer;
    private static String COMPROMISED_MESSAGE = "refresh token was already used";

    @Autowired
    public IssueTokenRefreshGrant(InsertTokenGraphRefreshGrant insertTokenGraphRefreshGrant, TokenChainRepository tokenChainRepository, ResourceOwnerTokenRepository resourceOwnerTokenRepository, TokenResponseBuilder tokenResponseBuilder, String issuer) {
        this.insertTokenGraphRefreshGrant = insertTokenGraphRefreshGrant;
        this.tokenChainRepository = tokenChainRepository;
        this.resourceOwnerTokenRepository = resourceOwnerTokenRepository;
        this.tokenResponseBuilder = tokenResponseBuilder;
        this.issuer = issuer;
    }

    public TokenResponse run(UUID clientId, UUID resourceOwnerId, UUID previousTokenId, UUID refreshTokenId, Token leadToken, List<Scope> scopes) throws CompromisedRefreshTokenException, ServerException {
        TokenGraph tokenGraph = insertTokenGraphRefreshGrant.insertTokenGraph(clientId, scopes, leadToken);

        // make relationships to the token graph.
        TokenChain tokenChain = makeTokenChain(tokenGraph.getToken(), previousTokenId, refreshTokenId);
        try {
            tokenChainRepository.insert(tokenChain);
        } catch (DuplicateRecordException e) {
            throw new CompromisedRefreshTokenException(COMPROMISED_MESSAGE, e);
        }

        ResourceOwnerToken resourceOwnerToken = makeResourceOwnerToken(resourceOwnerId, tokenGraph.getToken());
        resourceOwnerTokenRepository.insert(resourceOwnerToken);

        // build the response.
        List<String> audience = new ArrayList<>();
        audience.add(clientId.toString());

        TokenResponse tr = tokenResponseBuilder
                .setAccessToken(tokenGraph.getPlainTextAccessToken())
                .setRefreshAccessToken(tokenGraph.getPlainTextRefreshToken().get())
                .setTokenType(TokenType.BEARER)
                .setExpiresIn(tokenGraph.getToken().getSecondsToExpiration())
                .setExtension(tokenGraph.getExtension())
                .setIssuer(issuer)
                .setAudience(audience)
                .setIssuedAt(OffsetDateTime.now().toEpochSecond())
                .setExpirationTime(tokenGraph.getToken().getExpiresAt().toEpochSecond())
                .setAuthTime(leadToken.getCreatedAt().toEpochSecond())
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
}
