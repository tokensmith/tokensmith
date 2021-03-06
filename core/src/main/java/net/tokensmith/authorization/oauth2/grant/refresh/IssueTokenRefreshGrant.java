package net.tokensmith.authorization.oauth2.grant.refresh;

import net.tokensmith.authorization.exception.ServerException;
import net.tokensmith.authorization.oauth2.grant.refresh.exception.CompromisedRefreshTokenException;
import net.tokensmith.authorization.oauth2.grant.token.builder.TokenResponseBuilder;
import net.tokensmith.authorization.oauth2.grant.token.entity.TokenGraph;
import net.tokensmith.authorization.oauth2.grant.token.entity.TokenResponse;
import net.tokensmith.authorization.oauth2.grant.token.entity.TokenType;
import net.tokensmith.repository.entity.Client;
import net.tokensmith.repository.entity.RefreshToken;
import net.tokensmith.repository.entity.ResourceOwner;
import net.tokensmith.repository.entity.ResourceOwnerToken;
import net.tokensmith.repository.entity.Scope;
import net.tokensmith.repository.entity.Token;
import net.tokensmith.repository.entity.TokenChain;
import net.tokensmith.repository.exceptions.DuplicateRecordException;
import net.tokensmith.repository.repo.ResourceOwnerTokenRepository;
import net.tokensmith.repository.repo.TokenChainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Component
public class IssueTokenRefreshGrant {
    private InsertTokenGraphRefreshGrant insertTokenGraphRefreshGrant;
    private TokenChainRepository tokenChainRepository;
    private ResourceOwnerTokenRepository resourceOwnerTokenRepository;

    private String issuer;
    private static String COMPROMISED_MESSAGE = "refresh token was already used";

    @Autowired
    public IssueTokenRefreshGrant(InsertTokenGraphRefreshGrant insertTokenGraphRefreshGrant, TokenChainRepository tokenChainRepository, ResourceOwnerTokenRepository resourceOwnerTokenRepository, String issuer) {
        this.insertTokenGraphRefreshGrant = insertTokenGraphRefreshGrant;
        this.tokenChainRepository = tokenChainRepository;
        this.resourceOwnerTokenRepository = resourceOwnerTokenRepository;
        this.issuer = issuer;
    }

    public TokenResponse run(UUID clientId, UUID resourceOwnerId, UUID previousTokenId, UUID refreshTokenId, Token leadToken, List<Scope> scopes, List<Client> audience) throws CompromisedRefreshTokenException, ServerException {
        TokenGraph tokenGraph = insertTokenGraphRefreshGrant.insertTokenGraph(clientId, scopes, leadToken, audience);

        // make relationships to the token graph.
        TokenChain tokenChain = makeTokenChain(tokenGraph.getToken(), previousTokenId, refreshTokenId);
        try {
            tokenChainRepository.insert(tokenChain);
        } catch (DuplicateRecordException e) {
            throw new CompromisedRefreshTokenException(COMPROMISED_MESSAGE, e);
        }

        ResourceOwnerToken resourceOwnerToken = makeResourceOwnerToken(resourceOwnerId, tokenGraph.getToken());
        resourceOwnerTokenRepository.insert(resourceOwnerToken);

        List<String> responseAudience = tokenGraph.getToken().getAudience()
                .stream()
                .map(i->i.getId().toString())
                .collect(Collectors.toList());

        TokenResponse tr = new TokenResponseBuilder()
                .setAccessToken(tokenGraph.getPlainTextAccessToken())
                .setRefreshAccessToken(tokenGraph.getPlainTextRefreshToken().get())
                .setTokenType(TokenType.BEARER)
                .setExpiresIn(tokenGraph.getToken().getSecondsToExpiration())
                .setExtension(tokenGraph.getExtension())
                .setIssuer(issuer)
                .setAudience(responseAudience)
                .setIssuedAt(OffsetDateTime.now().toEpochSecond())
                .setExpirationTime(tokenGraph.getToken().getExpiresAt().toEpochSecond())
                .setAuthTime(leadToken.getCreatedAt().toEpochSecond())
                .nonce(tokenGraph.getToken().getNonce())
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
