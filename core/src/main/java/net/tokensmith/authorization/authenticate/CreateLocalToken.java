package net.tokensmith.authorization.authenticate;

import net.tokensmith.authorization.authenticate.exception.LocalSessionException;
import net.tokensmith.authorization.authenticate.model.Session;
import net.tokensmith.authorization.security.RandomString;
import net.tokensmith.authorization.security.ciphers.HashToken;
import net.tokensmith.repository.entity.LocalToken;
import net.tokensmith.repository.exceptions.DuplicateRecordException;
import net.tokensmith.repository.repo.LocalTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class CreateLocalToken {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateLocalToken.class);
    private static Integer MAX_RETRIES = 3;
    private RandomString randomString;
    private HashToken hashToken;
    private LocalTokenRepository localTokenRepository;
    private Long sessionExpirationInSeconds;

    @Autowired
    public CreateLocalToken(RandomString randomString, HashToken hashToken, LocalTokenRepository localTokenRepository, Long sessionExpirationInSeconds) {
        this.randomString = randomString;
        this.hashToken = hashToken;
        this.localTokenRepository = localTokenRepository;
        this.sessionExpirationInSeconds = sessionExpirationInSeconds;
    }

    public Session makeAndRevokeSession(UUID resourceOwnerId, int attempt) throws LocalSessionException {
        localTokenRepository.revokeActive(resourceOwnerId);
        return makeSession(resourceOwnerId, attempt);
    }

    public Session makeSession(UUID resourceOwnerId, int attempt) throws LocalSessionException {
        String accessToken = randomString.run();
        String hashedToken  = hashToken.run(accessToken);

        LocalToken localToken = new LocalToken.Builder()
                .id(UUID.randomUUID())
                .resourceOwnerId(resourceOwnerId)
                .token(hashedToken)
                .revoked(false)
                .expiresAt(OffsetDateTime.now().plusSeconds(sessionExpirationInSeconds))
                .createdAt(OffsetDateTime.now())
                .build();

        try {
            localTokenRepository.insert(localToken);
        } catch (DuplicateRecordException e) {
            LOGGER.warn(e.getMessage(), e);
            if (attempt <= MAX_RETRIES) {
                return makeSession(resourceOwnerId, attempt+1);
            } else {
                throw new LocalSessionException("attempted " + attempt + " to create local token", e);
            }
        }

        return new Session(accessToken, localToken.getCreatedAt().toEpochSecond());
    }
}
