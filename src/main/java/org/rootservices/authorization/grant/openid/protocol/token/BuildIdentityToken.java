package org.rootservices.authorization.grant.openid.protocol.token;

import org.rootservices.authorization.grant.openid.protocol.token.exception.IdTokenException;
import org.rootservices.authorization.grant.openid.protocol.token.exception.KeyNotFoundException;
import org.rootservices.authorization.grant.openid.protocol.token.exception.ResourceOwnerNotFoundException;

import java.util.List;

/**
 * Created by tommackenzie on 1/24/16.
 */
public interface BuildIdentityToken {
    String build(String accessToken) throws ResourceOwnerNotFoundException, IdTokenException, KeyNotFoundException;
}
