package org.rootservices.authorization.grant.code.protocol.token;

import org.rootservices.authorization.persistence.entity.Token;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 5/24/15.
 */
public interface CreateToken {
    Token run(UUID clientUUID, String clientSecret, String code, Optional<URI> redirectURI);
}
