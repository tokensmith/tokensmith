package net.tokensmith.authorization.oauth2.grant.token;

import net.tokensmith.authorization.persistence.entity.RefreshToken;
import net.tokensmith.authorization.persistence.entity.Token;
import net.tokensmith.authorization.security.ciphers.HashTextStaticSalt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Created by tommackenzie on 10/4/16.
 */
@Component
public class MakeRefreshToken {
    private HashTextStaticSalt hashText;

    @Autowired
    public MakeRefreshToken(HashTextStaticSalt hashText) {
        this.hashText = hashText;
    }

    public RefreshToken run(Token token, String plainTextToken, Long secondsToExpiration) {

        byte[] hashedToken = hashText.run(plainTextToken).getBytes();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(UUID.randomUUID());
        refreshToken.setToken(token);
        refreshToken.setAccessToken(hashedToken);
        refreshToken.setExpiresAt(OffsetDateTime.now().plusSeconds(secondsToExpiration));

        return refreshToken;
    }
}
