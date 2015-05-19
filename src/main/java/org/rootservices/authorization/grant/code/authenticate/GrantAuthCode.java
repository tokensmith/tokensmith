package org.rootservices.authorization.grant.code.authenticate;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 4/23/15.
 */
public interface GrantAuthCode {
    String run(UUID resourceOwnerUUID, UUID ClientUUID, Optional<URI> redirectURI, List<String> scopes);
    int getSecondsToExpiration();
}
