package org.rootservices.authorization.oauth2.grant.redirect.implicit.authorization.response;


import org.rootservices.authorization.oauth2.grant.redirect.implicit.authorization.response.entity.ImplicitAccessToken;
import org.rootservices.authorization.oauth2.grant.token.MakeBearerToken;
import org.rootservices.authorization.oauth2.grant.token.builder.TokenResponseBuilder;
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
    private MakeBearerToken makeBearerToken;
    private TokenRepository tokenRepository;
    private ScopeRepository scopeRepository;
    private TokenScopeRepository tokenScopeRepository;
    private ResourceOwnerTokenRepository resourceOwnerTokenRepository;
    private ClientTokenRepository clientTokenRepository;

    @Autowired
    public IssueTokenImplicitGrant(MakeBearerToken makeBearerToken, TokenRepository tokenRepository, ScopeRepository scopeRepository, TokenScopeRepository tokenScopeRepository, ResourceOwnerTokenRepository resourceOwnerTokenRepository, ClientTokenRepository clientTokenRepository) {
        this.makeBearerToken = makeBearerToken;
        this.tokenRepository = tokenRepository;
        this.scopeRepository = scopeRepository;
        this.tokenScopeRepository = tokenScopeRepository;
        this.resourceOwnerTokenRepository = resourceOwnerTokenRepository;
        this.clientTokenRepository = clientTokenRepository;
    }

    public Token run(UUID clientId, ResourceOwner resourceOwner, List<String> scopeNames, String plainTextAccessToken) {
        Token token = makeBearerToken.run(plainTextAccessToken);
        token.setGrantType(GrantType.TOKEN);

        try {
            tokenRepository.insert(token);
        } catch (DuplicateRecordException e) {
            // TODO: handle this exception.
            e.printStackTrace();
        }

        List<Scope> scopes = scopeRepository.findByNames(scopeNames);
        token.setTokenScopes(new ArrayList<>());

        for(Scope scope: scopes) {
            TokenScope ts = new TokenScope();
            ts.setId(UUID.randomUUID());
            ts.setTokenId(token.getId());
            ts.setScope(scope);
            tokenScopeRepository.insert(ts);

            token.getTokenScopes().add(ts);
        }

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

        return token;
    }

    public Long getSecondsToExpiration() {
        return makeBearerToken.getSecondsToExpiration();
    }
}
