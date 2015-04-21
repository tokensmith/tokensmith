package org.rootservices.authorization.grant.code.authenticate;

import org.rootservices.authorization.persistence.entity.AuthCode;
import org.rootservices.authorization.security.Hash;
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
    private Hash hash;

    public MakeAuthCodeImpl() {
    }

    public MakeAuthCodeImpl(Hash hash) {
        this.hash = hash;
    }

    @Override
    public AuthCode run(UUID resourceOwnerUUID, UUID clientUUID, String authorizationCode, int secondsToExpire) {

        String hashedAuthorizationCode = hash.run(authorizationCode);

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
