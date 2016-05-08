package org.rootservices.authorization.oauth2.grant.code.authorization.response.builder;

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
public class AuthCodeBuilderImpl implements AuthCodeBuilder {

    private HashTextStaticSalt hashText;

    @Autowired
    public AuthCodeBuilderImpl(HashTextStaticSalt hashText) {
        this.hashText = hashText;
    }

    @Override
    public AuthCode run(AccessRequest accessRequest, String authorizationCode, int secondsToExpire) {

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
