package org.rootservices.authorization.oauth2.grant.token;

import org.rootservices.authorization.oauth2.grant.token.entity.TokenType;
import org.rootservices.authorization.persistence.entity.RefreshToken;
import org.rootservices.authorization.security.HashTextStaticSalt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Created by tommackenzie on 10/4/16.
 */
@Component
public class MakeRefreshToken {
    private static final Long SECONDS_TO_EXPIRATION = 1209600L;
    private HashTextStaticSalt hashText;

    @Autowired
    public MakeRefreshToken(HashTextStaticSalt hashText) {
        this.hashText = hashText;
    }

    public RefreshToken run(UUID tokenId, String plainTextToken) {

        byte[] hashedToken = hashText.run(plainTextToken).getBytes();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(UUID.randomUUID());
        refreshToken.setTokenId(tokenId);
        refreshToken.setToken(hashedToken);
        refreshToken.setExpiresAt(OffsetDateTime.now().plusSeconds(SECONDS_TO_EXPIRATION));

        return refreshToken;
    }

    public Long getSecondsToExpiration() {
        return SECONDS_TO_EXPIRATION;
    }
}
