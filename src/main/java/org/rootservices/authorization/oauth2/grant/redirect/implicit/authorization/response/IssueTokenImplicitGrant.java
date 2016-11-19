package org.rootservices.authorization.oauth2.grant.redirect.implicit.authorization.response;


import org.rootservices.authorization.exception.ServerException;
import org.rootservices.authorization.oauth2.grant.redirect.implicit.authorization.InsertTokenGraphImplicitGrant;
import org.rootservices.authorization.oauth2.grant.redirect.implicit.authorization.response.entity.ImplicitAccessToken;
import org.rootservices.authorization.oauth2.grant.token.MakeBearerToken;
import org.rootservices.authorization.oauth2.grant.token.builder.TokenResponseBuilder;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenGraph;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenType;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by tommackenzie on 6/23/16.
 */
@Component
public class IssueTokenImplicitGrant {
    private InsertTokenGraphImplicitGrant insertTokenGraphImplicitGrant;
    private ScopeRepository scopeRepository;
    private ResourceOwnerTokenRepository resourceOwnerTokenRepository;
    private ClientTokenRepository clientTokenRepository;

    @Autowired
    public IssueTokenImplicitGrant(InsertTokenGraphImplicitGrant insertTokenGraphImplicitGrant, ScopeRepository scopeRepository, ResourceOwnerTokenRepository resourceOwnerTokenRepository, ClientTokenRepository clientTokenRepository) {
        this.insertTokenGraphImplicitGrant = insertTokenGraphImplicitGrant;
        this.scopeRepository = scopeRepository;
        this.resourceOwnerTokenRepository = resourceOwnerTokenRepository;
        this.clientTokenRepository = clientTokenRepository;
    }

    public TokenGraph run(UUID clientId, ResourceOwner resourceOwner, List<String> scopeNames) throws ServerException {

        List<Scope> scopes = scopeRepository.findByNames(scopeNames);
        TokenGraph tokenGraph = insertTokenGraphImplicitGrant.insertTokenGraph(scopes);

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

        return tokenGraph;
    }
}
