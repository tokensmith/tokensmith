package net.tokensmith.authorization.oauth2.grant.token;

import net.tokensmith.authorization.oauth2.grant.token.entity.TokenType;
import net.tokensmith.authorization.persistence.entity.GrantType;
import net.tokensmith.authorization.persistence.entity.Token;
import net.tokensmith.authorization.security.ciphers.HashTextStaticSalt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by tommackenzie on 6/2/15.
 */
@Component
public class MakeBearerToken {

    private static final TokenType TOKEN_TYPE = TokenType.BEARER;
    private HashTextStaticSalt hashText;

    @Autowired
    public MakeBearerToken(HashTextStaticSalt hashText) {
        this.hashText = hashText;
    }


    public Token run(UUID clientId, String plainTextToken, Long secondsToExpiration) {

        Token token = new Token();
        token.setId(UUID.randomUUID());

        byte[] hashedToken = hashText.run(plainTextToken).getBytes();
        token.setToken(hashedToken);
        token.setExpiresAt(OffsetDateTime.now().plusSeconds(secondsToExpiration));
        token.setSecondsToExpiration(secondsToExpiration);
        token.setGrantType(GrantType.AUTHORIZATION_CODE);
        token.setClientId(clientId);
        token.setTokenScopes(new ArrayList<>());
        token.setAudience(new ArrayList<>());

        return token;
    }

    public TokenType getTokenType() {
        return TOKEN_TYPE;
    }
}
