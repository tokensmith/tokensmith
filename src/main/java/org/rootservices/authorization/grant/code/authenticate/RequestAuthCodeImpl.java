package org.rootservices.authorization.grant.code.authenticate;

import org.rootservices.authorization.grant.code.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.grant.code.factory.AuthRequestFactory;
import org.rootservices.authorization.grant.code.request.AuthRequest;
import org.rootservices.authorization.persistence.entity.AuthCode;
import org.rootservices.authorization.persistence.repository.AuthCodeRepository;
import org.rootservices.authorization.persistence.repository.AuthRequestRepository;
import org.rootservices.authorization.security.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by tommackenzie on 4/16/15.
 */
@Component
public class RequestAuthCodeImpl implements RequestAuthCode {

    private static final int TWO_MINUTES = 120;
    @Autowired
    private LoginResourceOwner loginResourceOwner;
    @Autowired
    private RandomString randomString;
    @Autowired
    private MakeAuthCode makeAuthCode;
    @Autowired
    private AuthCodeRepository authCodeRepository;
    @Autowired
    private MakeAuthRequest makeAuthRequest;
    @Autowired
    private AuthRequestRepository authRequestRepository;

    public RequestAuthCodeImpl() {}

    public RequestAuthCodeImpl(LoginResourceOwner loginResourceOwner, RandomString randomString, MakeAuthCode makeAuthCode, AuthCodeRepository authCodeRepository, MakeAuthRequest makeAuthRequest, AuthRequestRepository authRequestRepository) {
        this.loginResourceOwner = loginResourceOwner;
        this.randomString = randomString;
        this.makeAuthCode = makeAuthCode;
        this.authCodeRepository = authCodeRepository;
        this.makeAuthRequest = makeAuthRequest;
        this.authRequestRepository = authRequestRepository;
    }

    @Override
    public String run(String userName, String plainTextPassword, AuthRequest authRequest) throws UnauthorizedException {
        UUID resourceOwnerUUID = loginResourceOwner.run(userName, plainTextPassword);

        // auth Code.
        String authorizationCode = randomString.run();
        AuthCode authCode = makeAuthCode.run(
                resourceOwnerUUID,
                authRequest.getClientId(),
                authorizationCode,
                TWO_MINUTES
        );
        authCodeRepository.insert(authCode);

        // auth request
        org.rootservices.authorization.persistence.entity.AuthRequest authRequestEntity = makeAuthRequest.run(authCode.getUuid(), authRequest);
        authRequestRepository.insert(authRequestEntity);

        return authorizationCode;
    }

    public int getSecondsToExpiration() {
        return TWO_MINUTES;
    }
}
