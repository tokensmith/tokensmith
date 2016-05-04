package org.rootservices.authorization.grant.code.protocol.token;

import org.rootservices.authorization.grant.code.protocol.token.response.TokenType;
import org.rootservices.authorization.persistence.entity.Token;

import java.util.UUID;

/**
 * Created by tommackenzie on 6/2/15.
 */
public interface MakeToken {
    Token run(String plainTextToken);
    Integer getSecondsToExpiration();
    TokenType getTokenType();
}
