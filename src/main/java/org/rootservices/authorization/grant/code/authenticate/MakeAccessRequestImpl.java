package org.rootservices.authorization.grant.code.authenticate;

import org.rootservices.authorization.persistence.entity.AccessRequest;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by tommackenzie on 4/20/15.
 */
@Component
public class MakeAccessRequestImpl implements MakeAccessRequest {

    @Override
    public AccessRequest run(UUID authCodeUUID, org.rootservices.authorization.grant.code.request.AuthRequest authRequest) {
        AccessRequest accessRequest = new AccessRequest();
        accessRequest.setUuid(UUID.randomUUID());
        accessRequest.setRedirectURI(authRequest.getRedirectURI());
        accessRequest.setAuthCodeUUID(authCodeUUID);

        return accessRequest;
    }
}
