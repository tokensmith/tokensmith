package org.rootservices.authorization.oauth2.grant.token;

import org.rootservices.authorization.oauth2.grant.token.entity.TokenType;
import org.rootservices.authorization.persistence.entity.RefreshToken;
import org.rootservices.authorization.persistence.entity.Token;
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
    private HashTextStaticSalt hashText;

    @Autowired
    public MakeRefreshToken(HashTextStaticSalt hashText) {
        this.hashText = hashText;
    }

    public RefreshToken run(Token token, Token headToken, String plainTextToken, Long secondsToExpiration) {

        byte[] hashedToken = hashText.run(plainTextToken).getBytes();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(UUID.randomUUID());
        refreshToken.setToken(token);
        refreshToken.setHeadToken(headToken);
        refreshToken.setAccessToken(hashedToken);
        refreshToken.setExpiresAt(OffsetDateTime.now().plusSeconds(secondsToExpiration));

        return refreshToken;
    }
}
