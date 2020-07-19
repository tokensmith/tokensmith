package net.tokensmith.authorization.oauth2.grant.redirect.implicit.authorization;


import net.tokensmith.authorization.exception.ServerException;
import net.tokensmith.authorization.oauth2.grant.token.InsertTokenGraph;
import net.tokensmith.authorization.oauth2.grant.token.MakeBearerToken;
import net.tokensmith.authorization.oauth2.grant.token.MakeRefreshToken;
import net.tokensmith.authorization.oauth2.grant.token.entity.TokenGraph;
import net.tokensmith.authorization.security.RandomString;
import net.tokensmith.repository.entity.Client;
import net.tokensmith.repository.entity.Configuration;
import net.tokensmith.repository.entity.GrantType;
import net.tokensmith.repository.entity.Scope;
import net.tokensmith.repository.repo.ConfigurationRepository;
import net.tokensmith.repository.repo.RefreshTokenRepository;
import net.tokensmith.repository.repo.TokenAudienceRepository;
import net.tokensmith.repository.repo.TokenRepository;
import net.tokensmith.repository.repo.TokenScopeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 11/19/16.
 */
@Component
public class InsertTokenGraphImplicitGrant extends InsertTokenGraph {
    protected static final Logger logger = LoggerFactory.getLogger(InsertTokenGraphImplicitGrant.class);

    @Autowired
    public InsertTokenGraphImplicitGrant(ConfigurationRepository configurationRepository, RandomString randomString, MakeBearerToken makeBearerToken, TokenRepository tokenRepository, MakeRefreshToken makeRefreshToken, RefreshTokenRepository refreshTokenRepository, TokenScopeRepository tokenScopeRepository, TokenAudienceRepository tokenAudienceRepository) {
        super(configurationRepository, randomString, makeBearerToken, tokenRepository, makeRefreshToken, refreshTokenRepository, tokenScopeRepository, tokenAudienceRepository);
    }

    @Override
    protected GrantType getGrantType() {
        return GrantType.TOKEN;
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected Long getSecondsToExpiration(Configuration configuration) {
        return configuration.getAccessTokenTokenSecondsToExpiry();
    }

    @Override
    public TokenGraph insertTokenGraph(UUID clientId, List<Scope> scopes, List<Client> audience, Optional<String> nonce) throws ServerException {
        Configuration config = configurationRepository.get();

        TokenGraph tokenGraph = insertToken(
                1,
                clientId,
                nonce,
                config.getId(),
                config.getAccessTokenSize(),
                getSecondsToExpiration(config),
                OffsetDateTime.now()
        );

        insertTokenScope(scopes, tokenGraph);
        insertTokenAudience(tokenGraph.getToken().getId(), audience);
        tokenGraph.getToken().setAudience(audience);

        return tokenGraph;
    }
}
