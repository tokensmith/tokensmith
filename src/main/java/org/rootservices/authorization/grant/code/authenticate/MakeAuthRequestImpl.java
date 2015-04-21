package org.rootservices.authorization.grant.code.authenticate;

import org.rootservices.authorization.persistence.entity.AuthRequest;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by tommackenzie on 4/20/15.
 */
@Component
public class MakeAuthRequestImpl implements MakeAuthRequest {

    @Override
    public AuthRequest run(UUID authCodeUUID, org.rootservices.authorization.grant.code.request.AuthRequest authRequest) {
        AuthRequest authRequestEntity = new AuthRequest();
        authRequestEntity.setUuid(UUID.randomUUID());
        authRequestEntity.setResponseType(authRequest.getResponseType());
        authRequestEntity.setRedirectURI(authRequest.getRedirectURI());
        authRequestEntity.setAuthCodeUUID(authCodeUUID);

        return authRequestEntity;
    }
}
