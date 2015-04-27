package org.rootservices.authorization.grant.code.authenticate;

import org.rootservices.authorization.persistence.entity.AccessRequest;
import org.rootservices.authorization.persistence.entity.AuthCode;
import org.rootservices.authorization.persistence.repository.AccessRequestRepository;
import org.rootservices.authorization.persistence.repository.AuthCodeRepository;
import org.rootservices.authorization.security.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 4/23/15.
 */
@Component
public class GrantAuthCodeImpl implements GrantAuthCode {
    private static final int SECONDS_TO_EXPIRATION = 120;
    @Autowired
    private RandomString randomString;
    @Autowired
    private MakeAuthCode makeAuthCode;
    @Autowired
    private AuthCodeRepository authCodeRepository;
    @Autowired
    private AccessRequestRepository accessRequestRepository;

    public GrantAuthCodeImpl() {}

    public GrantAuthCodeImpl(RandomString randomString, MakeAuthCode makeAuthCode, AuthCodeRepository authCodeRepository, AccessRequestRepository accessRequestRepository) {
        this.randomString = randomString;
        this.makeAuthCode = makeAuthCode;
        this.authCodeRepository = authCodeRepository;
        this.accessRequestRepository = accessRequestRepository;
    }

    public String run(UUID resourceOwnerUUID, UUID ClientUUID, Optional<URI> redirectURI) {

        String authorizationCode = randomString.run();
        AuthCode authCode = makeAuthCode.run(
                resourceOwnerUUID,
                ClientUUID,
                authorizationCode,
                SECONDS_TO_EXPIRATION
        );
        authCodeRepository.insert(authCode);


        AccessRequest accessRequest = new AccessRequest(
                UUID.randomUUID(), redirectURI, authCode.getUuid()
        );
        accessRequestRepository.insert(accessRequest);

        return authorizationCode;
    }

    public int getSecondsToExpiration() {
        return SECONDS_TO_EXPIRATION;
    }

}
