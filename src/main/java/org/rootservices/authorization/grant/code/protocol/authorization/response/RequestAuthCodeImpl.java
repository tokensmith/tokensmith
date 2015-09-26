package org.rootservices.authorization.grant.code.protocol.authorization.response;

import org.rootservices.authorization.grant.code.protocol.authorization.ValidateParams;
import org.rootservices.authorization.authenticate.LoginResourceOwner;
import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.grant.code.exception.InformClientException;
import org.rootservices.authorization.grant.code.exception.InformResourceOwnerException;
import org.rootservices.authorization.grant.code.protocol.authorization.exception.AuthCodeInsertException;
import org.rootservices.authorization.grant.code.protocol.authorization.request.AuthRequest;
import org.rootservices.authorization.grant.code.protocol.authorization.response.builder.AuthResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    private AuthResponseBuilder authResponseBuilder;

    public RequestAuthCodeImpl() {}

    public RequestAuthCodeImpl(ValidateParams validateParams, LoginResourceOwner loginResourceOwner, GrantAuthCode grantAuthCode, AuthResponseBuilder authResponseBuilder) {
        this.validateParams = validateParams;
        this.loginResourceOwner = loginResourceOwner;
        this.grantAuthCode = grantAuthCode;
        this.authResponseBuilder = authResponseBuilder;
    }

    @Override
    public AuthResponse run(AuthCodeInput input) throws UnauthorizedException, InformResourceOwnerException, InformClientException, AuthCodeInsertException {

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
            authRequest.getRedirectURI(),
            authRequest.getScopes()
        );

        AuthResponse authResponse = authResponseBuilder.run(
                authRequest.getClientId(),
                authorizationCode,
                authRequest.getState(),
                authRequest.getRedirectURI()
        );

        return authResponse;
    }
}
