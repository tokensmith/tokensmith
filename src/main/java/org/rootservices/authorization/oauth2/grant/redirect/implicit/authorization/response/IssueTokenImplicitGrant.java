package org.rootservices.authorization.oauth2.grant.redirect.implicit.authorization.response;


import org.rootservices.authorization.exception.ServerException;
import org.rootservices.authorization.oauth2.grant.redirect.implicit.authorization.InsertTokenGraphImplicitGrant;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenGraph;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Created by tommackenzie on 6/23/16.
 */
@Component
public class IssueTokenImplicitGrant {
    private InsertTokenGraphImplicitGrant insertTokenGraphImplicitGrant;
    private ScopeRepository scopeRepository;
    private ResourceOwnerTokenRepository resourceOwnerTokenRepository;

    @Autowired
    public IssueTokenImplicitGrant(InsertTokenGraphImplicitGrant insertTokenGraphImplicitGrant, ScopeRepository scopeRepository, ResourceOwnerTokenRepository resourceOwnerTokenRepository) {
        this.insertTokenGraphImplicitGrant = insertTokenGraphImplicitGrant;
        this.scopeRepository = scopeRepository;
        this.resourceOwnerTokenRepository = resourceOwnerTokenRepository;
    }

    public TokenGraph run(UUID clientId, ResourceOwner resourceOwner, List<String> scopeNames, List<Client> audience) throws ServerException {

        List<Scope> scopes = scopeRepository.findByNames(scopeNames);
        TokenGraph tokenGraph = insertTokenGraphImplicitGrant.insertTokenGraph(clientId, scopes, audience);

        ResourceOwnerToken resourceOwnerToken = new ResourceOwnerToken();
        resourceOwnerToken.setId(UUID.randomUUID());
        resourceOwnerToken.setResourceOwner(resourceOwner);
        resourceOwnerToken.setToken(tokenGraph.getToken());
        resourceOwnerTokenRepository.insert(resourceOwnerToken);

        return tokenGraph;
    }
}
