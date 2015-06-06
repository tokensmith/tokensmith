package org.rootservices.authorization.grant.code.protocol.authorization;

import org.rootservices.authorization.grant.code.exception.InformResourceOwnerException;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 4/29/15.
 */
public interface MakeAuthResponse {
    AuthResponse run(UUID clientUUID, String authCode, Optional<String> state, Optional<URI> redirectUri) throws InformResourceOwnerException;
}
