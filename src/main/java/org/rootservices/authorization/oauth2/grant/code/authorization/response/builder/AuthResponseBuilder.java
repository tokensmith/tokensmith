package org.rootservices.authorization.oauth2.grant.code.authorization.response.builder;

import org.rootservices.authorization.oauth2.grant.code.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.oauth2.grant.code.authorization.response.AuthResponse;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 4/29/15.
 */
public interface AuthResponseBuilder {
    AuthResponse run(UUID clientUUID, String authCode, Optional<String> state, Optional<URI> redirectUri) throws InformResourceOwnerException;
}
