package org.rootservices.authorization.grant.code.authenticate;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 4/23/15.
 */
public interface GrantAuthCode {
    public String run(UUID resourceOwnerUUID, UUID ClientUUID, Optional<URI> redirectURI);
    public int getSecondsToExpiration();
}
