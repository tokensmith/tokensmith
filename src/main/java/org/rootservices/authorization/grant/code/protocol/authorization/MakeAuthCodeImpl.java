package org.rootservices.authorization.grant.code.protocol.authorization;

import org.rootservices.authorization.persistence.entity.AuthCode;
import org.rootservices.authorization.security.TextHasher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Created by tommackenzie on 4/17/15.
 */
@Component
public class MakeAuthCodeImpl implements MakeAuthCode {


    public MakeAuthCodeImpl() {}

    @Override
    public AuthCode run(UUID resourceOwnerUUID, UUID clientUUID, String authorizationCode, int secondsToExpire) {

        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hash algorithm not found");
        }

        byte[] hashedAuthorizationCode = digest.digest(authorizationCode.getBytes());

        OffsetDateTime expiresAt = OffsetDateTime.now();
        expiresAt = expiresAt.plusSeconds(secondsToExpire);

        AuthCode authCode = new AuthCode(
                UUID.randomUUID(),
                hashedAuthorizationCode,
                resourceOwnerUUID,
                clientUUID,
                expiresAt
        );

        return authCode;
    }
}
