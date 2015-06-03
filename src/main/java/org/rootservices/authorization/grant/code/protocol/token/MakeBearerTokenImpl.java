package org.rootservices.authorization.grant.code.protocol.token;

import org.rootservices.authorization.persistence.entity.Token;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Created by tommackenzie on 6/2/15.
 */
@Component
public class MakeBearerTokenImpl implements MakeToken {
    private static final Integer SECONDS_TO_EXPIRATION = 3600;
    private static final TokenType TOKEN_TYPE = TokenType.BEARER;

    @Override
    public Token run(UUID authCodeUUID, String plainTextToken) {

        Token token = new Token();
        token.setUuid(UUID.randomUUID());
        token.setAuthCodeUUID(authCodeUUID);

        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hash algorithm not found");
        }

        byte[] hashedToken = digest.digest(plainTextToken.getBytes());
        token.setToken(hashedToken);
        token.setExpiresAt(OffsetDateTime.now().plusSeconds(SECONDS_TO_EXPIRATION));

        return token;
    }

    @Override
    public Integer getSecondsToExpiration() {
        return SECONDS_TO_EXPIRATION;
    }

    @Override
    public TokenType getTokenType() {
        return TOKEN_TYPE;
    }
}
