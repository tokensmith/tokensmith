package org.rootservices.authorization.grant.code.protocol.authorization;

import org.rootservices.authorization.persistence.entity.AuthCode;
import org.rootservices.authorization.security.TextHasher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Created by tommackenzie on 4/17/15.
 */
@Component
public class MakeAuthCodeImpl implements MakeAuthCode {

    @Autowired
    private TextHasher textHasher;

    public MakeAuthCodeImpl() {
    }

    public MakeAuthCodeImpl(TextHasher textHasher) {
        this.textHasher = textHasher;
    }

    @Override
    public AuthCode run(UUID resourceOwnerUUID, UUID clientUUID, String authorizationCode, int secondsToExpire) {

        String hashedAuthorizationCode = textHasher.run(authorizationCode);

        OffsetDateTime expiresAt = OffsetDateTime.now();
        expiresAt = expiresAt.plusSeconds(secondsToExpire);

        AuthCode authCode = new AuthCode(
                UUID.randomUUID(),
                hashedAuthorizationCode.getBytes(),
                resourceOwnerUUID,
                clientUUID,
                expiresAt
        );

        return authCode;
    }
}
