package org.rootservices.authorization.oauth2.grant.redirect.implicit.authorization.response;

import org.rootservices.authorization.oauth2.grant.redirect.code.token.MakeToken;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.repository.ResourceOwnerTokenRepository;
import org.rootservices.authorization.persistence.repository.TokenRepository;
import org.rootservices.authorization.persistence.repository.TokenScopeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Created by tommackenzie on 6/23/16.
 */
@Component
public class GrantToken {
    private MakeToken makeToken;
    private TokenRepository tokenRepository;
    private TokenScopeRepository tokenScopeRepository;
    private ResourceOwnerTokenRepository resourceOwnerTokenRepository;

    @Autowired
    public GrantToken(MakeToken makeToken, TokenRepository tokenRepository, TokenScopeRepository tokenScopeRepository, ResourceOwnerTokenRepository resourceOwnerTokenRepository) {
        this.makeToken = makeToken;
        this.tokenRepository = tokenRepository;
        this.tokenScopeRepository = tokenScopeRepository;
        this.resourceOwnerTokenRepository = resourceOwnerTokenRepository;
    }

    public Token grant(ResourceOwner resourceOwner, List<Scope> scopes, String plainTextAccessToken) {
        Token token = makeToken.run(plainTextAccessToken);
        token.setGrantType(GrantType.TOKEN);

        try {
            tokenRepository.insert(token);
        } catch (DuplicateRecordException e) {
            // TODO - should this retry?
            e.printStackTrace();
        }

        for(Scope scope: scopes) {
            TokenScope ts = new TokenScope();
            ts.setId(UUID.randomUUID());
            ts.setTokenId(token.getUuid());
            ts.setScope(scope);
            tokenScopeRepository.insert(ts);
        }

        ResourceOwnerToken resourceOwnerToken = new ResourceOwnerToken();
        resourceOwnerToken.setId(UUID.randomUUID());
        resourceOwnerToken.setResourceOwner(resourceOwner);
        resourceOwnerToken.setToken(token);
        resourceOwnerTokenRepository.insert(resourceOwnerToken);

        return token;
    }

    public Long getSecondsToExpiration() {
        return makeToken.getSecondsToExpiration();
    }
}
