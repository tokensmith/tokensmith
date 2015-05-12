package org.rootservices.authorization.grant.code.authenticate;

import org.rootservices.authorization.grant.ValidateParams;
import org.rootservices.authorization.grant.code.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.grant.code.exception.InformClientException;
import org.rootservices.authorization.grant.code.exception.InformResourceOwnerException;
import org.rootservices.authorization.grant.code.request.AuthRequest;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * Created by tommackenzie on 4/16/15.
 *
 * Section 4.1.2
 */
@Component
public class RequestAuthCodeImpl implements RequestAuthCode {

    @Autowired
    private ValidateParams validateParams;
    @Autowired
    private LoginResourceOwner loginResourceOwner;
    @Autowired
    private GrantAuthCode grantAuthCode;
    @Autowired
    private MakeAuthResponse makeAuthResponse;

    public RequestAuthCodeImpl() {}

    public RequestAuthCodeImpl(ValidateParams validateParams, LoginResourceOwner loginResourceOwner, GrantAuthCode grantAuthCode, MakeAuthResponse makeAuthResponse) {
        this.validateParams = validateParams;
        this.loginResourceOwner = loginResourceOwner;
        this.grantAuthCode = grantAuthCode;
        this.makeAuthResponse = makeAuthResponse;
    }

    @Override
    public AuthResponse run(AuthCodeInput input) throws UnauthorizedException, InformResourceOwnerException, InformClientException {

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

        AuthResponse authResponse = makeAuthResponse.run(
                authRequest.getClientId(),
                authorizationCode,
                authRequest.getState(),
                authRequest.getRedirectURI()
        );

        return authResponse;
    }
}