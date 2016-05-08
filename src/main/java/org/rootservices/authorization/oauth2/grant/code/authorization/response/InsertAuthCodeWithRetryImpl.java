package org.rootservices.authorization.oauth2.grant.code.authorization.response;

import org.rootservices.authorization.oauth2.grant.code.authorization.response.exception.AuthCodeInsertException;
import org.rootservices.authorization.oauth2.grant.code.authorization.response.builder.AuthCodeBuilder;
import org.rootservices.authorization.persistence.entity.AccessRequest;
import org.rootservices.authorization.persistence.entity.AuthCode;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.repository.AuthCodeRepository;
import org.rootservices.authorization.security.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by tommackenzie on 7/16/15.
 */
@Component
public class InsertAuthCodeWithRetryImpl implements InsertAuthCodeWithRetry {
    private static final int SECONDS_TO_EXPIRATION = 120;
    private static final int MAX_RETRY_ATTEMPTS = 3;

    private RandomString randomString;
    private AuthCodeBuilder authCodeBuilder;
    private AuthCodeRepository authCodeRepository;

    @Autowired
    public InsertAuthCodeWithRetryImpl(RandomString randomString, AuthCodeBuilder authCodeBuilder, AuthCodeRepository authCodeRepository) {
        this.randomString = randomString;
        this.authCodeBuilder = authCodeBuilder;
        this.authCodeRepository = authCodeRepository;
    }

    @Override
    public String run(AccessRequest accessRequest, int attemptNumber) throws AuthCodeInsertException {
        String authorizationCode = randomString.run();
        AuthCode authCode = authCodeBuilder.run(
                accessRequest,
                authorizationCode,
                SECONDS_TO_EXPIRATION
        );

        try {
            authCodeRepository.insert(authCode);
        } catch(DuplicateRecordException e) {
            if (attemptNumber <= MAX_RETRY_ATTEMPTS) {
                return run(accessRequest, attemptNumber + 1);
            } else {
                throw new AuthCodeInsertException(
                    "Unique constraint on auth_code.code was violated."
                );
            }
        }
        return authorizationCode;
    }

    @Override
    public int getSecondsToExpiration() {
        return SECONDS_TO_EXPIRATION;
    }
}
