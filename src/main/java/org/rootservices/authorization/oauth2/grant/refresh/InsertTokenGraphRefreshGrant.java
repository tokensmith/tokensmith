package org.rootservices.authorization.oauth2.grant.refresh;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rootservices.authorization.exception.ServerException;
import org.rootservices.authorization.oauth2.grant.token.InsertTokenGraph;
import org.rootservices.authorization.oauth2.grant.token.MakeBearerToken;
import org.rootservices.authorization.oauth2.grant.token.MakeRefreshToken;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenGraph;
import org.rootservices.authorization.persistence.entity.Configuration;
import org.rootservices.authorization.persistence.entity.GrantType;
import org.rootservices.authorization.persistence.entity.Scope;
import org.rootservices.authorization.persistence.entity.Token;
import org.rootservices.authorization.persistence.repository.ConfigurationRepository;
import org.rootservices.authorization.persistence.repository.RefreshTokenRepository;
import org.rootservices.authorization.persistence.repository.TokenRepository;
import org.rootservices.authorization.persistence.repository.TokenScopeRepository;
import org.rootservices.authorization.security.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by tommackenzie on 11/18/16.
 */
@Component
public class InsertTokenGraphRefreshGrant extends InsertTokenGraph {
    protected static final Logger logger = LogManager.getLogger(InsertTokenGraphRefreshGrant.class);

    @Autowired
    public InsertTokenGraphRefreshGrant(ConfigurationRepository configurationRepository, RandomString randomString, MakeBearerToken makeBearerToken, TokenRepository tokenRepository, MakeRefreshToken makeRefreshToken, RefreshTokenRepository refreshTokenRepository, TokenScopeRepository tokenScopeRepository) {
        super(configurationRepository, randomString, makeBearerToken, tokenRepository, makeRefreshToken, refreshTokenRepository, tokenScopeRepository);
    }

    @Override
    protected GrantType getGrantType() {
        return GrantType.REFRESSH;
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    public TokenGraph insertTokenGraph(List<Scope> scopes, Token headToken) throws ServerException {
        Configuration config = configurationRepository.get();

        TokenGraph tokenGraph = insertToken(
                1,
                config.getId(),
                config.getAccessTokenSize(),
                config.getAccessTokenCodeSecondsToExpiry()
        );

        insertRefreshToken(
                1,
                config.getId(),
                config.getRefreshTokenSize(),
                config.getRefreshTokenSecondsToExpiry(),
                tokenGraph,
                headToken
        );

        insertTokenScope(scopes, tokenGraph);

        return tokenGraph;
    }
}
