package org.rootservices.authorization.grant.code.protocol.token;

import org.rootservices.authorization.persistence.entity.Token;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 5/24/15.
 */
@Component
public class CreateTokenImpl implements CreateToken {

    @Override
    public Token run(UUID clientUUID, String clientPassword, String code, Optional<URI> redirectURI) {
        return null;
    }
}
