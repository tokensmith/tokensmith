package org.rootservices.authorization.oauth2.grant.redirect.code.token;

import org.rootservices.authorization.oauth2.grant.redirect.code.token.response.TokenType;
import org.rootservices.authorization.persistence.entity.Token;

/**
 * Created by tommackenzie on 6/2/15.
 */
public interface MakeToken {
    Token run(String plainTextToken);
    Integer getSecondsToExpiration();
    TokenType getTokenType();
}
