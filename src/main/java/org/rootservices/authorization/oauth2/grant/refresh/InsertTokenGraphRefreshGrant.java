package org.rootservices.authorization.oauth2.grant.refresh;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rootservices.authorization.exception.ServerException;
import org.rootservices.authorization.oauth2.grant.token.InsertTokenGraph;
import org.rootservices.authorization.oauth2.grant.token.MakeBearerToken;
import org.rootservices.authorization.oauth2.grant.token.MakeRefreshToken;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenGraph;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.repository.*;
import org.rootservices.authorization.security.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Created by tommackenzie on 11/18/16.
 */
@Component
public class InsertTokenGraphRefreshGrant extends InsertTokenGraph {
    protected static final Logger logger = LogManager.getLogger(InsertTokenGraphRefreshGrant.class);

    private TokenLeadTokenRepository tokenLeadTokenRepository;

    @Autowired
    public InsertTokenGraphRefreshGrant(ConfigurationRepository configurationRepository, RandomString randomString, MakeBearerToken makeBearerToken, TokenRepository tokenRepository, MakeRefreshToken makeRefreshToken, RefreshTokenRepository refreshTokenRepository, TokenScopeRepository tokenScopeRepository, TokenLeadTokenRepository tokenLeadTokenRepository, TokenAudienceRepository tokenAudienceRepository) {
        super(configurationRepository, randomString, makeBearerToken, tokenRepository, makeRefreshToken, refreshTokenRepository, tokenScopeRepository, tokenAudienceRepository);
        this.tokenLeadTokenRepository = tokenLeadTokenRepository;
    }

    @Override
    protected GrantType getGrantType() {
        return GrantType.REFRESSH;
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected Long getSecondsToExpiration(Configuration configuration) {
        return configuration.getAccessTokenRefreshSecondsToExpiry();
    }

    public TokenGraph insertTokenGraph(UUID clientId, List<Scope> scopes, Token leadToken) throws ServerException {
        Configuration config = configurationRepository.get();

        TokenGraph tokenGraph = insertToken(
                1,
                clientId,
                config.getId(),
                config.getAccessTokenSize(),
                getSecondsToExpiration(config)
        );

        UUID tokenId = tokenGraph.getToken().getId();
        insertTokenLeadToken(tokenId, leadToken.getId());

        insertRefreshToken(
                1,
                config.getId(),
                config.getRefreshTokenSize(),
                config.getRefreshTokenSecondsToExpiry(),
                tokenGraph
        );

        insertTokenScope(scopes, tokenGraph);
        insertTokenAudience(tokenGraph.getToken().getId(), clientId);

        return tokenGraph;
    }

    public void insertTokenLeadToken(UUID tokenId, UUID leadTokenId) {
        TokenLeadToken tlt = new TokenLeadToken();
        tlt.setId(UUID.randomUUID());
        tlt.setTokenId(tokenId);
        tlt.setLeadTokenId(leadTokenId);

        tokenLeadTokenRepository.insert(tlt);
    }
}
