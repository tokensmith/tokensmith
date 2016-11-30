package org.rootservices.authorization.oauth2.grant.password;

import org.rootservices.authorization.exception.ServerException;
import org.rootservices.authorization.oauth2.grant.token.MakeBearerToken;
import org.rootservices.authorization.oauth2.grant.token.MakeRefreshToken;
import org.rootservices.authorization.oauth2.grant.token.builder.TokenResponseBuilder;
import org.rootservices.authorization.oauth2.grant.token.entity.Extension;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenGraph;
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
    private InsertTokenGraphPasswordGrant insertTokenGraphPasswordGrant;
    private ResourceOwnerTokenRepository resourceOwnerTokenRepository;
    private ClientTokenRepository clientTokenRepository;
    private TokenResponseBuilder tokenResponseBuilder;

    private String issuer;

    public IssueTokenPasswordGrant(InsertTokenGraphPasswordGrant insertTokenGraphPasswordGrant, ResourceOwnerTokenRepository resourceOwnerTokenRepository, ClientTokenRepository clientTokenRepository, TokenResponseBuilder tokenResponseBuilder, String issuer) {
        this.insertTokenGraphPasswordGrant = insertTokenGraphPasswordGrant;
        this.resourceOwnerTokenRepository = resourceOwnerTokenRepository;
        this.clientTokenRepository = clientTokenRepository;
        this.tokenResponseBuilder = tokenResponseBuilder;
        this.issuer = issuer;
    }

    public TokenResponse run(UUID clientId, UUID resourceOwnerId, List<Scope> scopes) throws ServerException {
        TokenGraph tokenGraph = insertTokenGraphPasswordGrant.insertTokenGraph(clientId, scopes);

        ResourceOwner resourceOwner = new ResourceOwner();
        resourceOwner.setId(resourceOwnerId);
        ResourceOwnerToken resourceOwnerToken = new ResourceOwnerToken();
        resourceOwnerToken.setId(UUID.randomUUID());
        resourceOwnerToken.setResourceOwner(resourceOwner);
        resourceOwnerToken.setToken(tokenGraph.getToken());

        resourceOwnerTokenRepository.insert(resourceOwnerToken);

        ClientToken clientToken = new ClientToken();
        clientToken.setId(UUID.randomUUID());
        clientToken.setClientId(clientId);
        clientToken.setTokenId(tokenGraph.getToken().getId());

        clientTokenRepository.insert(clientToken);

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
                .setAuthTime(tokenGraph.getToken().getCreatedAt().toEpochSecond())
                .build();
        return tr;
    }
}
