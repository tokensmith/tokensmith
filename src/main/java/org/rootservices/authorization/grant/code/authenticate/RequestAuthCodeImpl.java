package org.rootservices.authorization.grant.code.authenticate;

import org.rootservices.authorization.grant.ValidateParams;
import org.rootservices.authorization.grant.code.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.grant.code.authenticate.input.AuthCodeInput;
import org.rootservices.authorization.grant.code.exception.InformClientException;
import org.rootservices.authorization.grant.code.exception.InformResourceOwnerException;
import org.rootservices.authorization.grant.code.request.AuthRequest;
import org.rootservices.authorization.grant.code.request.ValidAuthRequest;
import org.rootservices.authorization.persistence.entity.AccessRequest;
import org.rootservices.authorization.persistence.entity.AuthCode;
import org.rootservices.authorization.persistence.repository.AuthCodeRepository;
import org.rootservices.authorization.persistence.repository.AccessRequestRepository;
import org.rootservices.authorization.security.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 4/16/15.
 */
@Component
public class RequestAuthCodeImpl implements RequestAuthCode {

    @Autowired
    private ValidateParams validateParams;
    @Autowired
    private LoginResourceOwner loginResourceOwner;
    @Autowired
    private GrantAuthCode grantAuthCode;

    public RequestAuthCodeImpl() {}

    public RequestAuthCodeImpl(ValidateParams validateParams, LoginResourceOwner loginResourceOwner, GrantAuthCode grantAuthCode) {
        this.validateParams = validateParams;
        this.loginResourceOwner = loginResourceOwner;
        this.grantAuthCode = grantAuthCode;
    }

    @Override
    public String run(AuthCodeInput input) throws UnauthorizedException, InformResourceOwnerException, InformClientException {

        AuthRequest authRequest = validateParams.run(
            input.getClientIds(),
            input.getResponseTypes(),
            input.getRedirectUris(),
            input.getScopes(),
            input.getStates()
        );

        UUID resourceOwnerUUID = loginResourceOwner.run(
            input.getUserName(), input.getPlainTextPassword()
        );

        String authorizationCode = grantAuthCode.run(
            resourceOwnerUUID,
            authRequest.getClientId(),
            authRequest.getRedirectURI()
        );

        return authorizationCode;
    }
}
