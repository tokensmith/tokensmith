package org.rootservices.authorization.grant.code.protocol.token.validator;

import org.rootservices.authorization.grant.code.protocol.token.TokenRequest;
import org.rootservices.authorization.grant.code.protocol.token.factory.exception.InvalidValueException;
import org.rootservices.authorization.grant.code.protocol.token.factory.exception.MissingKeyException;

/**
 * Created by tommackenzie on 7/4/15.
 */
public interface IsTokenRequestValid {
    boolean run(TokenRequest tokenRequest) throws InvalidValueException, MissingKeyException;
}
