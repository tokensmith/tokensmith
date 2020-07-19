package net.tokensmith.authorization.oauth2.grant.redirect.code.authorization.response.factory;

import net.tokensmith.authorization.security.ciphers.HashToken;
import net.tokensmith.repository.entity.AccessRequest;
import net.tokensmith.repository.entity.AuthCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Created by tommackenzie on 4/17/15.
 */
@Component
public class AuthCodeFactory {

    private HashToken hashToken;

    @Autowired
    public AuthCodeFactory(HashToken hashToken) {
        this.hashToken = hashToken;
    }

    public AuthCode makeAuthCode(AccessRequest accessRequest, String authorizationCode, Long secondsToExpire) {

        String hashedAuthorizationCode = hashToken.run(authorizationCode);

        OffsetDateTime expiresAt = OffsetDateTime.now();
        expiresAt = expiresAt.plusSeconds(secondsToExpire);

        AuthCode authCode = new AuthCode(
                UUID.randomUUID(),
                hashedAuthorizationCode,
                accessRequest,
                expiresAt
        );

        return authCode;
    }
}
