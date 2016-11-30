package org.rootservices.authorization.oauth2.grant.token;

import org.apache.logging.log4j.Logger;
import org.rootservices.authorization.exception.ServerException;
import org.rootservices.authorization.oauth2.grant.token.entity.Extension;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenGraph;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.repository.ConfigurationRepository;
import org.rootservices.authorization.persistence.repository.RefreshTokenRepository;
import org.rootservices.authorization.persistence.repository.TokenRepository;
import org.rootservices.authorization.persistence.repository.TokenScopeRepository;
import org.rootservices.authorization.security.RandomString;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class InsertTokenGraph {

    protected ConfigurationRepository configurationRepository;
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

    public InsertTokenGraph(ConfigurationRepository configurationRepository, RandomString randomString, MakeBearerToken makeBearerToken, TokenRepository tokenRepository, MakeRefreshToken makeRefreshToken, RefreshTokenRepository refreshTokenRepository, TokenScopeRepository tokenScopeRepository) {
        this.configurationRepository = configurationRepository;
        this.randomString = randomString;
        this.makeBearerToken = makeBearerToken;
        this.tokenRepository = tokenRepository;
        this.makeRefreshToken = makeRefreshToken;
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenScopeRepository = tokenScopeRepository;
    }

    protected abstract GrantType getGrantType();
    protected abstract Logger getLogger();
    protected abstract Long getSecondsToExpiration(Configuration configuration);

    public TokenGraph insertTokenGraph(UUID clientId, List<Scope> scopes) throws ServerException {
        Configuration config = configurationRepository.get();

        TokenGraph tokenGraph = insertToken(
                1,
                clientId,
                config.getId(),
                config.getAccessTokenSize(),
                getSecondsToExpiration(config)
        );

        insertRefreshToken(
                1,
                config.getId(),
                config.getRefreshTokenSize(),
                config.getRefreshTokenSecondsToExpiry(),
                tokenGraph,
                tokenGraph.getToken()
        );

        insertTokenScope(scopes, tokenGraph);

        return tokenGraph;
    }

    public TokenGraph insertToken(Integer attempt, UUID clientId, UUID configId, Integer atSize, Long secondsToExpiration) throws ServerException {

        String plainTextToken = randomString.run(atSize);
        Token token = makeBearerToken.run(clientId, plainTextToken, secondsToExpiration);
        token.setGrantType(getGrantType());

        try {
            tokenRepository.insert(token);
        } catch( DuplicateRecordException e) {
            return handleDuplicateToken(e, attempt, clientId, configId, atSize, secondsToExpiration);
        }

        TokenGraph tokenGraph = new TokenGraph();
        tokenGraph.setToken(token);
        tokenGraph.setPlainTextAccessToken(plainTextToken);
        tokenGraph.setRefreshTokenId(Optional.empty());
        tokenGraph.setPlainTextRefreshToken(Optional.empty());

        return tokenGraph;
    }

    public void insertRefreshToken(Integer attempt, UUID configId, Integer atSize, Long secondsToExpiration, TokenGraph tokenGraph, Token headToken) throws ServerException {

        String refreshAccessToken = randomString.run(atSize);
        RefreshToken refreshToken = makeRefreshToken.run(tokenGraph.getToken(), headToken, refreshAccessToken, secondsToExpiration);
        try {
            refreshTokenRepository.insert(refreshToken);
        } catch (DuplicateRecordException e) {
            handleDuplicateRefreshToken(e, attempt, configId, atSize, secondsToExpiration, tokenGraph, headToken);
            return;
        }

        tokenGraph.setRefreshTokenId(Optional.of(refreshToken.getId()));
        tokenGraph.setPlainTextRefreshToken(Optional.of(refreshAccessToken));
    }

    public void insertTokenScope(List<Scope> scopes, TokenGraph tokenGraph) {

        Extension extension = Extension.NONE;
        for(Scope scope: scopes) {
            TokenScope ts = new TokenScope();
            ts.setId(UUID.randomUUID());
            ts.setTokenId(tokenGraph.getToken().getId());
            ts.setScope(scope);

            if (OPENID_SCOPE.equalsIgnoreCase(ts.getScope().getName())) {
                extension = Extension.IDENTITY;
            }
            tokenScopeRepository.insert(ts);
            tokenGraph.getToken().getTokenScopes().add(ts);
            // TODO: 134265317: needs a test
        }
        tokenGraph.setExtension(extension);
    }

    public TokenGraph handleDuplicateToken(DuplicateRecordException e, Integer attempt, UUID clientId, UUID configId, Integer atSize, Long secondsToExpiration) throws ServerException {
        Boolean isDuplicateToken = e.getKey().isPresent() && TOKEN_KEY.equals(e.getKey().get());

        if(isDuplicateToken && attempt < MAX_ATTEMPTS) {
            getLogger().warn(e.getMessage(), e);
            configurationRepository.updateAccessTokenSize(configId, atSize+1);
            return insertToken(attempt+1, clientId, configId, atSize+1, secondsToExpiration);
        } else if (isDuplicateToken && attempt >= MAX_ATTEMPTS) {
            String msg = String.format(KEY_KNOWN_FAILED_MSG, TOKEN_SCHEMA, attempt, atSize);
            getLogger().error(msg, e);
            throw new ServerException(msg, e);
        } else {
            String msg = String.format(KEY_UNKNOWN_FAILED_MSG, TOKEN_SCHEMA, e.getKey().orElse(UNKNOWN_KEY), attempt, atSize);
            getLogger().error(msg, e);
            throw new ServerException(msg, e);
        }
    }

    public void handleDuplicateRefreshToken(DuplicateRecordException e, Integer attempt, UUID configId, Integer atSize, Long secondsToExpiration, TokenGraph tokenGraph, Token headToken) throws ServerException {
        tokenRepository.revokeById(tokenGraph.getToken().getId());
        Boolean isDuplicateToken = e.getKey().isPresent() && REFRESH_TOKEN_KEY.equals(e.getKey().get());

        if(isDuplicateToken && attempt < MAX_ATTEMPTS) {
            getLogger().warn(e.getMessage(), e);
            configurationRepository.updateRefreshTokenSize(configId, atSize+1);
            insertRefreshToken(attempt+1, configId, atSize+1, secondsToExpiration, tokenGraph, headToken);
        } else if (isDuplicateToken && attempt >= MAX_ATTEMPTS) {
            String msg = String.format(KEY_KNOWN_FAILED_MSG, REFRESH_TOKEN_SCHEMA, attempt-1, atSize);
            getLogger().error(msg, e);
            throw new ServerException(msg, e);
        } else {
            String msg = String.format(KEY_UNKNOWN_FAILED_MSG, REFRESH_TOKEN_SCHEMA, e.getKey().orElse(UNKNOWN_KEY), attempt-1, atSize);
            getLogger().error(msg, e);
            throw new ServerException(msg, e);
        }
    }
}
