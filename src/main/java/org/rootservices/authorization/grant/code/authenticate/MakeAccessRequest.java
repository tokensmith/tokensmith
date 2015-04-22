package org.rootservices.authorization.grant.code.authenticate;

import org.rootservices.authorization.persistence.entity.AccessRequest;

import java.util.UUID;

/**
 * Created by tommackenzie on 4/20/15.
 */
public interface MakeAccessRequest {
    AccessRequest run(UUID authCodeUUID, org.rootservices.authorization.grant.code.request.AuthRequest authRequest);
}
