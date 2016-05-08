package org.rootservices.authorization.oauth2.grant.code.token.validator;

import org.rootservices.authorization.oauth2.grant.code.token.TokenRequest;
import org.rootservices.authorization.oauth2.grant.code.token.validator.exception.InvalidValueException;
import org.rootservices.authorization.oauth2.grant.code.token.validator.exception.MissingKeyException;

/**
 * Created by tommackenzie on 7/4/15.
 */
public interface IsTokenRequestValid {
    boolean run(TokenRequest tokenRequest) throws InvalidValueException, MissingKeyException;
}
