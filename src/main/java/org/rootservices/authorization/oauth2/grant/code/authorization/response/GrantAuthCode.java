package org.rootservices.authorization.oauth2.grant.code.authorization.response;

import org.rootservices.authorization.oauth2.grant.code.authorization.response.exception.AuthCodeInsertException;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 4/23/15.
 */
public interface GrantAuthCode {
    String run(UUID resourceOwnerId, UUID ClientUUID, Optional<URI> redirectURI, List<String> scopes) throws AuthCodeInsertException;
}
