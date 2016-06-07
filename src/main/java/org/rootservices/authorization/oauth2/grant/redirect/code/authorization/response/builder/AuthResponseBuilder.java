package org.rootservices.authorization.oauth2.grant.redirect.code.authorization.response.builder;

import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.oauth2.grant.redirect.code.authorization.response.AuthResponse;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 4/29/15.
 */
public interface AuthResponseBuilder {
    AuthResponse run(UUID clientUUID, String authCode, Optional<String> state, Optional<URI> redirectUri) throws InformResourceOwnerException;
}
