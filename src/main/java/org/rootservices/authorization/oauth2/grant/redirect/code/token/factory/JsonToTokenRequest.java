package org.rootservices.authorization.oauth2.grant.redirect.code.token.factory;

import org.rootservices.authorization.oauth2.grant.redirect.code.token.TokenRequest;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.factory.exception.DuplicateKeyException;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.validator.exception.InvalidValueException;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.validator.exception.MissingKeyException;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.factory.exception.InvalidPayloadException;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.factory.exception.UnknownKeyException;

import java.io.BufferedReader;

/**
 * Created by tommackenzie on 6/28/15.
 */
public interface JsonToTokenRequest {
    TokenRequest run(BufferedReader json) throws DuplicateKeyException, InvalidPayloadException, InvalidValueException, MissingKeyException, UnknownKeyException;
}
