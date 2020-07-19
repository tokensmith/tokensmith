package net.tokensmith.authorization.oauth2.grant.password;

import net.tokensmith.authorization.oauth2.grant.token.InsertTokenGraph;
import net.tokensmith.authorization.oauth2.grant.token.MakeBearerToken;
import net.tokensmith.authorization.oauth2.grant.token.MakeRefreshToken;
import net.tokensmith.authorization.security.RandomString;
import net.tokensmith.repository.entity.Configuration;
import net.tokensmith.repository.entity.GrantType;
import net.tokensmith.repository.repo.ConfigurationRepository;
import net.tokensmith.repository.repo.RefreshTokenRepository;
import net.tokensmith.repository.repo.TokenAudienceRepository;
import net.tokensmith.repository.repo.TokenRepository;
import net.tokensmith.repository.repo.TokenScopeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by tommackenzie on 11/15/16.
 */
@Component
public class InsertTokenGraphPasswordGrant extends InsertTokenGraph {
    protected static final Logger logger = LoggerFactory.getLogger(InsertTokenGraphPasswordGrant.class);

    @Autowired
    public InsertTokenGraphPasswordGrant(ConfigurationRepository configurationRepository, RandomString randomString, MakeBearerToken makeBearerToken, TokenRepository tokenRepository, MakeRefreshToken makeRefreshToken, RefreshTokenRepository refreshTokenRepository, TokenScopeRepository tokenScopeRepository, TokenAudienceRepository tokenAudienceRepository) {
        super(configurationRepository, randomString, makeBearerToken, tokenRepository, makeRefreshToken, refreshTokenRepository, tokenScopeRepository, tokenAudienceRepository);
    }

    @Override
    protected GrantType getGrantType() {
        return GrantType.PASSWORD;
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected Long getSecondsToExpiration(Configuration configuration) {
        return configuration.getAccessTokenPasswordSecondsToExpiry();
    }
}
