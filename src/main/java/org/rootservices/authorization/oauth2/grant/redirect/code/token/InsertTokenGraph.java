package org.rootservices.authorization.oauth2.grant.redirect.code.token;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.authorization.exception.ServerException;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.entity.TokenGraph;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.exception.CompromisedCodeException;
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
    private static String TOKEN_KEY = "token";
    private static String REFRESH_TOKEN_KEY = "access_token";
    private static Integer MAX_ATTEMPTS = 2;
    private static String KEY_KNOWN_FAILED_MSG = "Failed to insert %s. Attempted %s times. Token size is, %s.";
    private static String KEY_UNKNOWN_FAILED_MSG = "Failed to insert %s. Unknown key, %s. Did not retry. Attempted %s times. Token size is, %s.";

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

    public TokenGraph insertTokenGraph(List<AccessRequestScope> accessRequestScopes) {
        Configuration config = configurationRepository.get();

        TokenGraph tokenGraph = insertToken(config.getAccessTokenSize());
        insertRefreshToken(config.getRefreshTokenSize(), tokenGraph);
        insertTokenScope(accessRequestScopes, tokenGraph);

        return tokenGraph;
    }

    protected TokenGraph insertToken(Integer atSize) {

        String plainTextToken = randomString.run(atSize);
        Token token = makeBearerToken.run(plainTextToken);
        try {
            tokenRepository.insert(token);
        } catch( DuplicateRecordException e) {
            // handle
        }

        TokenGraph tokenGraph = new TokenGraph();
        tokenGraph.setToken(token);
        tokenGraph.setPlainTextAccessToken(plainTextToken);

        return tokenGraph;
    }

    protected void insertRefreshToken(Integer atSize, TokenGraph tokenGraph) {

        String refreshAccessToken = randomString.run(atSize);
        RefreshToken refreshToken = makeRefreshToken.run(tokenGraph.getToken(), tokenGraph.getToken(), refreshAccessToken);
        try {
            refreshTokenRepository.insert(refreshToken);
        } catch (DuplicateRecordException e) {
            // handle
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

    protected TokenGraph handleDuplicateToken(DuplicateRecordException e, Integer attempt, UUID configId, Integer tokenSize, List<AccessRequestScope> accessRequestScopes) throws ServerException, CompromisedCodeException {
        Boolean isDuplicateToken = e.getKey().isPresent() && TOKEN_KEY.equals(e.getKey().get());

        if(isDuplicateToken && attempt < MAX_ATTEMPTS) {
            logger.warn(e.getMessage(), e);
            configurationRepository.updateAccessTokenSize(configId, tokenSize+1);
            // return insertTokenGraph(attempt+1, accessRequestScopes);
            return null;
        } else if (isDuplicateToken && attempt >= MAX_ATTEMPTS) {
            String msg = String.format(KEY_KNOWN_FAILED_MSG, "token", attempt-1, tokenSize);
            logger.error(msg, e);
            throw new ServerException(msg, e);
        } else {
            String msg = String.format(KEY_UNKNOWN_FAILED_MSG, "token", e.getKey().orElse("unknown"), attempt-1, tokenSize);
            logger.error(msg, e);
            throw new ServerException(msg, e);
        }
    }

    protected TokenGraph handleDuplicateRefreshToken(DuplicateRecordException e, Integer attempt, UUID configId, Integer tokenSize, UUID tokenId, List<AccessRequestScope> accessRequestScopes) throws ServerException, CompromisedCodeException {
        tokenRepository.revokeById(tokenId);
        Boolean isDuplicateToken = e.getKey().isPresent() && REFRESH_TOKEN_KEY.equals(e.getKey().get());

        if(isDuplicateToken && attempt < MAX_ATTEMPTS) {
            logger.warn(e.getMessage(), e);
            configurationRepository.updateRefreshTokenSize(configId, tokenSize+1);
            // return insertTokenGraph(attempt+1, accessRequestScopes);
            return null;
        } else if (isDuplicateToken && attempt >= MAX_ATTEMPTS) {
            String msg = String.format(KEY_KNOWN_FAILED_MSG, "refresh_token", attempt-1, tokenSize);
            logger.error(msg, e);
            throw new ServerException(msg, e);
        } else {
            String msg = String.format(KEY_UNKNOWN_FAILED_MSG, "refresh_token", e.getKey().orElse("unknown"), attempt-1, tokenSize);
            logger.error(msg, e);
            throw new ServerException(msg, e);
        }
    }
}
