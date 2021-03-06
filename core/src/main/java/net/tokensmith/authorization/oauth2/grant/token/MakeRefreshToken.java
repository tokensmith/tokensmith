package net.tokensmith.authorization.oauth2.grant.token;


import net.tokensmith.authorization.security.ciphers.HashToken;
import net.tokensmith.repository.entity.RefreshToken;
import net.tokensmith.repository.entity.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Created by tommackenzie on 10/4/16.
 */
@Component
public class MakeRefreshToken {
    private HashToken hashToken;

    @Autowired
    public MakeRefreshToken(HashToken hashToken) {
        this.hashToken = hashToken;
    }

    public RefreshToken run(Token token, String plainTextToken, Long secondsToExpiration) {

        String hashedToken = hashToken.run(plainTextToken);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(UUID.randomUUID());
        refreshToken.setToken(token);
        refreshToken.setAccessToken(hashedToken);
        refreshToken.setExpiresAt(OffsetDateTime.now().plusSeconds(secondsToExpiration));

        return refreshToken;
    }
}
