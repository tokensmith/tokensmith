package org.rootservices.authorization.openId.identity;

import org.rootservices.authorization.openId.identity.exception.IdTokenException;
import org.rootservices.authorization.openId.identity.exception.KeyNotFoundException;
import org.rootservices.authorization.openId.identity.exception.ProfileNotFoundException;
import org.rootservices.authorization.openId.identity.exception.AccessRequestNotFoundException;

/**
 * Created by tommackenzie on 1/24/16.
 */
public interface BuildIdentityToken {
    String build(String accessToken) throws AccessRequestNotFoundException, IdTokenException, KeyNotFoundException, ProfileNotFoundException;
}
