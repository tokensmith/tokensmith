package org.rootservices.authorization.authenticate;

import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ConfidentialClient;

import java.util.UUID;

/**
 * Created by tommackenzie on 5/25/15.
 */
public interface LoginConfidentialClient {
    ConfidentialClient run(UUID clientUUID, String plainTextPassword) throws UnauthorizedException;
}
