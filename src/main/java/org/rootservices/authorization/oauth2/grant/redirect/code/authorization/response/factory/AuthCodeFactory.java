package org.rootservices.authorization.oauth2.grant.redirect.code.authorization.response.factory;

import org.rootservices.authorization.persistence.entity.AccessRequest;
import org.rootservices.authorization.persistence.entity.AuthCode;
import org.rootservices.authorization.security.HashTextStaticSalt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Created by tommackenzie on 4/17/15.
 */
@Component
public class AuthCodeFactory {

    private HashTextStaticSalt hashText;

    @Autowired
    public AuthCodeFactory(HashTextStaticSalt hashText) {
        this.hashText = hashText;
    }

    public AuthCode makeAuthCode(AccessRequest accessRequest, String authorizationCode, Long secondsToExpire) {

        byte[] hashedAuthorizationCode = hashText.run(authorizationCode).getBytes();;

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
