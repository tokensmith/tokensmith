package org.rootservices.authorization.oauth2.grant.redirect.code.token;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rootservices.authorization.oauth2.grant.token.InsertTokenGraph;
import org.rootservices.authorization.oauth2.grant.token.MakeBearerToken;
import org.rootservices.authorization.oauth2.grant.token.MakeRefreshToken;
import org.rootservices.authorization.persistence.entity.Configuration;
import org.rootservices.authorization.persistence.entity.GrantType;
import org.rootservices.authorization.persistence.repository.*;
import org.rootservices.authorization.security.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Created by tommackenzie on 11/13/16.
 */
@Component
public class InsertTokenGraphCodeGrant extends InsertTokenGraph {
    protected static final Logger logger = LogManager.getLogger(InsertTokenGraphCodeGrant.class);

    @Autowired
    public InsertTokenGraphCodeGrant(ConfigurationRepository configurationRepository, RandomString randomString, MakeBearerToken makeBearerToken, TokenRepository tokenRepository, MakeRefreshToken makeRefreshToken, RefreshTokenRepository refreshTokenRepository, TokenScopeRepository tokenScopeRepository, TokenAudienceRepository tokenAudienceRepository) {
        super(configurationRepository, randomString, makeBearerToken, tokenRepository, makeRefreshToken, refreshTokenRepository, tokenScopeRepository, tokenAudienceRepository);
    }

    @Override
    protected GrantType getGrantType() {
        return GrantType.AUTHORIZATION_CODE;
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected Long getSecondsToExpiration(Configuration configuration) {
        return configuration.getAccessTokenCodeSecondsToExpiry();
    }
}
