package org.rootservices.authorization.grant.code.protocol.token.factory;

import org.rootservices.authorization.grant.code.protocol.token.TokenInput;
import org.rootservices.authorization.grant.code.protocol.token.TokenRequest;

import java.util.List;

/**
 * Created by tommackenzie on 6/26/15.
 */
public interface TokenRequestFactory {
    TokenRequest makeTokenRequest(TokenInput tokenInput);
}
