package org.rootservices.authorization.oauth2.grant.redirect.code.token;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.authorization.exception.ServerException;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.entity.TokenGraph;
import org.rootservices.authorization.oauth2.grant.token.MakeBearerToken;
import org.rootservices.authorization.oauth2.grant.token.MakeRefreshToken;
import org.rootservices.authorization.oauth2.grant.token.entity.Extension;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.repository.ConfigurationRepository;
import org.rootservices.authorization.persistence.repository.RefreshTokenRepository;
import org.rootservices.authorization.persistence.repository.TokenRepository;
import org.rootservices.authorization.persistence.repository.TokenScopeRepository;
import org.rootservices.authorization.security.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Created by tommackenzie on 11/13/16.
 */
@Component
public class InsertTokenGraph {
    private static final Logger logger = LogManager.getLogger(InsertTokenGraph.class);

    private ConfigurationRepository configurationRepository;
    private RandomString randomString;
    private MakeBearerToken makeBearerToken;
    private TokenRepository tokenRepository;
    private MakeRefreshToken makeRefreshToken;
    private RefreshTokenRepository refreshTokenRepository;
    private TokenScopeRepository tokenScopeRepository;

    private static String OPENID_SCOPE = "openid";
    private static String TOKEN_SCHEMA = "token";
    private static String TOKEN_KEY = "token";
    private static String REFRESH_TOKEN_SCHEMA = "refresh_token";
    private static String REFRESH_TOKEN_KEY = "access_token";
    private static Integer MAX_ATTEMPTS = 2;
    private static String KEY_KNOWN_FAILED_MSG = "Failed to insert %s. Attempted %s times. Token size is, %s.";
    private static String KEY_UNKNOWN_FAILED_MSG = "Failed to insert %s. Unknown key, %s. Did not retry. Attempted %s times. Token size is, %s.";
    private static String UNKNOWN_KEY = "unknown";

    @Autowired
    public InsertTokenGraph(ConfigurationRepository configurationRepository, RandomString randomString, MakeBearerToken makeBearerToken, TokenRepository tokenRepository, MakeRefreshToken makeRefreshToken, RefreshTokenRepository refreshTokenRepository, TokenScopeRepository tokenScopeRepository) {
        this.configurationRepository = configurationRepository;
        this.randomString = randomString;
        this.makeBearerToken = makeBearerToken;
        this.tokenRepository = tokenRepository;
        this.makeRefreshToken = makeRefreshToken;
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenScopeRepository = tokenScopeRepository;
    }

    public TokenGraph insertTokenGraph(List<AccessRequestScope> accessRequestScopes) throws ServerException {
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
            tokenGraph
        );

        insertTokenScope(accessRequestScopes, tokenGraph);

        return tokenGraph;
    }

    protected TokenGraph insertToken(Integer attempt, UUID configId, Integer atSize, Long secondsToExpiration) throws ServerException {

        String plainTextToken = randomString.run(atSize);
        Token token = makeBearerToken.run(plainTextToken, secondsToExpiration);
        try {
            tokenRepository.insert(token);
        } catch( DuplicateRecordException e) {
            return handleDuplicateToken(e, attempt+1, configId, atSize, secondsToExpiration);
        }

        TokenGraph tokenGraph = new TokenGraph();
        tokenGraph.setToken(token);
        tokenGraph.setPlainTextAccessToken(plainTextToken);

        return tokenGraph;
    }

    protected void insertRefreshToken(Integer attempt, UUID configId, Integer atSize, Long secondsToExpiration, TokenGraph tokenGraph) throws ServerException {

        String refreshAccessToken = randomString.run(atSize);
        RefreshToken refreshToken = makeRefreshToken.run(tokenGraph.getToken(), tokenGraph.getToken(), refreshAccessToken, secondsToExpiration);
        try {
            refreshTokenRepository.insert(refreshToken);
        } catch (DuplicateRecordException e) {
            handleDuplicateRefreshToken(e, attempt+1, configId, atSize, secondsToExpiration, tokenGraph);
            return;
        }

        tokenGraph.setRefreshTokenId(refreshToken.getId());
        tokenGraph.setPlainTextRefreshToken(refreshAccessToken);
    }

    protected void insertTokenScope(List<AccessRequestScope> accessRequestScopes, TokenGraph tokenGraph) {

        Extension extension = Extension.NONE;
        for(AccessRequestScope ars: accessRequestScopes) {
            TokenScope ts = new TokenScope();
            ts.setId(UUID.randomUUID());
            ts.setTokenId(tokenGraph.getToken().getId());
            ts.setScope(ars.getScope());

            if (OPENID_SCOPE.equalsIgnoreCase(ts.getScope().getName())) {
                extension = Extension.IDENTITY;
            }
            tokenScopeRepository.insert(ts);
        }
        tokenGraph.setExtension(extension);
    }

    protected TokenGraph handleDuplicateToken(DuplicateRecordException e, Integer attempt, UUID configId, Integer atSize, Long secondsToExpiration) throws ServerException {
        Boolean isDuplicateToken = e.getKey().isPresent() && TOKEN_KEY.equals(e.getKey().get());

        if(isDuplicateToken && attempt <= MAX_ATTEMPTS) {
            logger.warn(e.getMessage(), e);
            configurationRepository.updateAccessTokenSize(configId, atSize+1);
            return insertToken(attempt+1, configId, atSize+1, secondsToExpiration);
        } else if (isDuplicateToken && attempt >= MAX_ATTEMPTS) {
            String msg = String.format(KEY_KNOWN_FAILED_MSG, TOKEN_SCHEMA, attempt-1, atSize);
            logger.error(msg, e);
            throw new ServerException(msg, e);
        } else {
            String msg = String.format(KEY_UNKNOWN_FAILED_MSG, TOKEN_SCHEMA, e.getKey().orElse(UNKNOWN_KEY), attempt-1, atSize);
            logger.error(msg, e);
            throw new ServerException(msg, e);
        }
    }

    protected void handleDuplicateRefreshToken(DuplicateRecordException e, Integer attempt, UUID configId, Integer atSize, Long secondsToExpiration, TokenGraph tokenGraph) throws ServerException {
        tokenRepository.revokeById(tokenGraph.getToken().getId());
        Boolean isDuplicateToken = e.getKey().isPresent() && REFRESH_TOKEN_KEY.equals(e.getKey().get());

        if(isDuplicateToken && attempt <= MAX_ATTEMPTS) {
            logger.warn(e.getMessage(), e);
            configurationRepository.updateRefreshTokenSize(configId, atSize+1);
            insertRefreshToken(attempt+1, configId, atSize+1, secondsToExpiration, tokenGraph);
        } else if (isDuplicateToken && attempt >= MAX_ATTEMPTS) {
            String msg = String.format(KEY_KNOWN_FAILED_MSG, REFRESH_TOKEN_SCHEMA, attempt-1, atSize);
            logger.error(msg, e);
            throw new ServerException(msg, e);
        } else {
            String msg = String.format(KEY_UNKNOWN_FAILED_MSG, REFRESH_TOKEN_SCHEMA, e.getKey().orElse(UNKNOWN_KEY), attempt-1, atSize);
            logger.error(msg, e);
            throw new ServerException(msg, e);
        }
    }
}
