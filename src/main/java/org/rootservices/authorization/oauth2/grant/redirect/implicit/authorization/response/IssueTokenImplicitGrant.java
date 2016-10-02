package org.rootservices.authorization.oauth2.grant.redirect.implicit.authorization.response;


import org.rootservices.authorization.oauth2.grant.redirect.code.token.MakeBearerToken;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.repository.ResourceOwnerTokenRepository;
import org.rootservices.authorization.persistence.repository.ScopeRepository;
import org.rootservices.authorization.persistence.repository.TokenRepository;
import org.rootservices.authorization.persistence.repository.TokenScopeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    @Autowired
    public IssueTokenImplicitGrant(MakeBearerToken makeBearerToken, TokenRepository tokenRepository, ScopeRepository scopeRepository, TokenScopeRepository tokenScopeRepository, ResourceOwnerTokenRepository resourceOwnerTokenRepository) {
        this.makeBearerToken = makeBearerToken;
        this.tokenRepository = tokenRepository;
        this.scopeRepository = scopeRepository;
        this.tokenScopeRepository = tokenScopeRepository;
        this.resourceOwnerTokenRepository = resourceOwnerTokenRepository;
    }

    public Token run(ResourceOwner resourceOwner, List<String> scopeNames, String plainTextAccessToken) {
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

        return token;
    }

    public Long getSecondsToExpiration() {
        return makeBearerToken.getSecondsToExpiration();
    }
}
