package org.rootservices.authorization.oauth2.grant.redirect.code.authorization.response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rootservices.authorization.oauth2.grant.redirect.code.authorization.response.factory.AuthCodeFactory;
import org.rootservices.authorization.oauth2.grant.redirect.code.authorization.response.exception.AuthCodeInsertException;
import org.rootservices.authorization.persistence.entity.AccessRequest;
import org.rootservices.authorization.persistence.entity.AuthCode;
import org.rootservices.authorization.persistence.entity.Configuration;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.repository.AuthCodeRepository;
import org.rootservices.authorization.persistence.repository.ConfigurationRepository;
import org.rootservices.authorization.security.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by tommackenzie on 7/16/15.
 */
@Component
public class InsertAuthCodeWithRetry {
    private static final Logger logger = LogManager.getLogger(InsertAuthCodeWithRetry.class);
    private static final int SECONDS_TO_EXPIRATION = 120;
    private static final int MAX_RETRY_ATTEMPTS = 2;

    private ConfigurationRepository configurationRepository;
    private RandomString randomString;
    private AuthCodeFactory authCodeFactory;
    private AuthCodeRepository authCodeRepository;

    private static String CODE_KEY = "code";
    private static String RETRY_MSG = "Retrying to insert auth_code. Last attempt, %s. Last code size, %s. Next code size, %s.";
    private static String KEY_KNOWN_FAILED_MSG = "Failed to insert auth_code. Attempted %s times. code size is, %s.";
    private static String KEY_UNKNOWN_FAILED_MSG = "Failed to insert auth_code. Unknown key, %s. Did not retry. Attempted %s times. code size is, %s.";
    private static String UNKNOWN_KEY = "unknown";

    @Autowired
    public InsertAuthCodeWithRetry(ConfigurationRepository configurationRepository, RandomString randomString, AuthCodeFactory authCodeFactory, AuthCodeRepository authCodeRepository) {
        this.configurationRepository = configurationRepository;
        this.randomString = randomString;
        this.authCodeFactory = authCodeFactory;
        this.authCodeRepository = authCodeRepository;
    }

    public String run(AccessRequest accessRequest) throws AuthCodeInsertException {
        Configuration config = configurationRepository.get();
        return insertAuthorizationCode(
                1,
                config.getId(),
                config.getAuthorizationCodeSize(),
                config.getAuthorizationCodeSecondsToExpiry(),
                accessRequest
        );
    }

    protected String insertAuthorizationCode(int attempt, UUID configId, Integer codeSize, Long secondsToExpiration, AccessRequest accessRequest) throws AuthCodeInsertException {
        String authorizationCode = randomString.run(codeSize);
        AuthCode authCode = authCodeFactory.makeAuthCode(
                accessRequest,
                authorizationCode,
                secondsToExpiration
        );

        try {
            authCodeRepository.insert(authCode);
        } catch(DuplicateRecordException e) {
            return handleDuplicateRecordException(e, attempt, configId, codeSize, secondsToExpiration, accessRequest);
        }

        return authorizationCode;
    }

    public String handleDuplicateRecordException(DuplicateRecordException e, int attempt, UUID configId, Integer codeSize, Long secondsToExpiration, AccessRequest accessRequest) throws AuthCodeInsertException {
        Boolean isDuplicateCode = e.getKey().isPresent() && CODE_KEY.equals(e.getKey().get());
        if (isDuplicateCode && attempt < MAX_RETRY_ATTEMPTS) {
            String msg = String.format(RETRY_MSG, attempt, codeSize, codeSize+1);
            logger.warn(msg);
            logger.warn(e.getMessage(), e);
            configurationRepository.updateAuthorizationCodeSize(configId, codeSize+1);
            return insertAuthorizationCode(attempt+1, configId, codeSize+1, secondsToExpiration, accessRequest);
        } else if (isDuplicateCode && attempt >= MAX_RETRY_ATTEMPTS) {
            String msg = String.format(KEY_KNOWN_FAILED_MSG, attempt, codeSize);
            logger.error(msg);
            logger.error(e.getMessage(), e);
            throw new AuthCodeInsertException(msg, e);
        } else {
            String msg = String.format(KEY_UNKNOWN_FAILED_MSG, e.getKey().orElse(UNKNOWN_KEY), attempt-1, codeSize);
            logger.error(msg);
            logger.error(e.getMessage(), e);
            throw new AuthCodeInsertException(msg, e);
        }
    }

    public int getSecondsToExpiration() {
        return SECONDS_TO_EXPIRATION;
    }
}
