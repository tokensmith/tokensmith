package org.rootservices.authorization.grant.code.authenticate;

import org.rootservices.authorization.grant.code.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.grant.code.request.AuthRequest;
import org.rootservices.authorization.persistence.entity.AccessRequest;
import org.rootservices.authorization.persistence.entity.AuthCode;
import org.rootservices.authorization.persistence.repository.AuthCodeRepository;
import org.rootservices.authorization.persistence.repository.AccessRequestRepository;
import org.rootservices.authorization.security.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by tommackenzie on 4/16/15.
 */
@Component
public class RequestAuthCodeImpl implements RequestAuthCode {

    private static final int SECONDS_TO_EXPIRATION = 120;
    @Autowired
    private LoginResourceOwner loginResourceOwner;
    @Autowired
    private RandomString randomString;
    @Autowired
    private MakeAuthCode makeAuthCode;
    @Autowired
    private AuthCodeRepository authCodeRepository;
    @Autowired
    private MakeAccessRequest makeAccessRequest;
    @Autowired
    private AccessRequestRepository accessRequestRepository;

    public RequestAuthCodeImpl() {}

    public RequestAuthCodeImpl(LoginResourceOwner loginResourceOwner, RandomString randomString, MakeAuthCode makeAuthCode, AuthCodeRepository authCodeRepository, MakeAccessRequest makeAccessRequest, AccessRequestRepository accessRequestRepository) {
        this.loginResourceOwner = loginResourceOwner;
        this.randomString = randomString;
        this.makeAuthCode = makeAuthCode;
        this.authCodeRepository = authCodeRepository;
        this.makeAccessRequest = makeAccessRequest;
        this.accessRequestRepository = accessRequestRepository;
    }

    @Override
    public String run(String userName, String plainTextPassword, AuthRequest authRequest) throws UnauthorizedException {
        UUID resourceOwnerUUID = loginResourceOwner.run(userName, plainTextPassword);

        // auth Code
        String authorizationCode = randomString.run();
        AuthCode authCode = makeAuthCode.run(
                resourceOwnerUUID,
                authRequest.getClientId(),
                authorizationCode,
                SECONDS_TO_EXPIRATION
        );
        authCodeRepository.insert(authCode);

        // access request
        AccessRequest accessRequest = makeAccessRequest.run(authCode.getUuid(), authRequest);
        accessRequestRepository.insert(accessRequest);

        return authorizationCode;
    }

    public int getSecondsToExpiration() {
        return SECONDS_TO_EXPIRATION;
    }
}
