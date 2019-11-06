package net.tokensmith.authorization.oauth2.grant.token;

import net.tokensmith.authorization.oauth2.grant.token.entity.TokenType;
import net.tokensmith.authorization.security.ciphers.HashToken;
import net.tokensmith.repository.entity.GrantType;
import net.tokensmith.repository.entity.Token;
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
    private HashToken hashToken;

    @Autowired
    public MakeBearerToken(HashToken hashToken) {
        this.hashToken = hashToken;
    }


    public Token run(UUID clientId, String plainTextToken, Long secondsToExpiration) {

        Token token = new Token();
        token.setId(UUID.randomUUID());

        String hashedToken = hashToken.run(plainTextToken);
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
