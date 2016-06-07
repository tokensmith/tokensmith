package org.rootservices.authorization.oauth2.grant.redirect.code.token;

import org.rootservices.authorization.oauth2.grant.redirect.code.token.response.TokenType;
import org.rootservices.authorization.persistence.entity.GrantType;
import org.rootservices.authorization.persistence.entity.Token;
import org.rootservices.authorization.security.HashTextStaticSalt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Created by tommackenzie on 6/2/15.
 */
@Component
public class MakeBearerTokenImpl implements MakeToken {

    private static final Integer SECONDS_TO_EXPIRATION = 3600;
    private static final TokenType TOKEN_TYPE = TokenType.BEARER;
    private HashTextStaticSalt hashText;

    @Autowired
    public MakeBearerTokenImpl(HashTextStaticSalt hashText) {
        this.hashText = hashText;
    }

    @Override
    public Token run(String plainTextToken) {

        Token token = new Token();
        token.setUuid(UUID.randomUUID());

        byte[] hashedToken = hashText.run(plainTextToken).getBytes();
        token.setToken(hashedToken);
        token.setExpiresAt(OffsetDateTime.now().plusSeconds(SECONDS_TO_EXPIRATION));
        token.setGrantType(GrantType.AUTHORIZATION_CODE);

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
