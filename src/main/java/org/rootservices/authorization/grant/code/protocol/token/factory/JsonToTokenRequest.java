package org.rootservices.authorization.grant.code.protocol.token.factory;

import org.rootservices.authorization.grant.code.protocol.token.TokenRequest;
import org.rootservices.authorization.grant.code.protocol.token.factory.exception.*;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.net.URI;
import java.util.Map;
import java.util.Optional;

/**
 * Created by tommackenzie on 6/28/15.
 */
public interface JsonToTokenRequest {
    TokenRequest run(BufferedReader json) throws DuplicateKeyException, InvalidPayloadException, InvalidValueException, MissingKeyException, UnknownKeyException;
}
