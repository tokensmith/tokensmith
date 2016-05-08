package org.rootservices.authorization.oauth2.grant.code.token.factory;

import org.rootservices.authorization.oauth2.grant.code.token.TokenRequest;
import org.rootservices.authorization.oauth2.grant.code.token.factory.exception.*;
import org.rootservices.authorization.oauth2.grant.code.token.validator.exception.InvalidValueException;
import org.rootservices.authorization.oauth2.grant.code.token.validator.exception.MissingKeyException;

import java.io.BufferedReader;

/**
 * Created by tommackenzie on 6/28/15.
 */
public interface JsonToTokenRequest {
    TokenRequest run(BufferedReader json) throws DuplicateKeyException, InvalidPayloadException, InvalidValueException, MissingKeyException, UnknownKeyException;
}
