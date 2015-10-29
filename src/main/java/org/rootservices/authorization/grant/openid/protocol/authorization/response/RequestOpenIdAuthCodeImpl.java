package org.rootservices.authorization.grant.openid.protocol.authorization.response;

import org.rootservices.authorization.authenticate.LoginResourceOwner;
import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.grant.code.exception.InformClientException;
import org.rootservices.authorization.grant.code.exception.InformResourceOwnerException;
import org.rootservices.authorization.grant.code.protocol.authorization.exception.AuthCodeInsertException;
import org.rootservices.authorization.grant.code.protocol.authorization.response.*;
import org.rootservices.authorization.grant.code.protocol.authorization.response.builder.AuthResponseBuilder;
import org.rootservices.authorization.grant.openid.protocol.authorization.request.ValidateOpenIdParams;
import org.rootservices.authorization.grant.openid.protocol.authorization.request.entity.OpenIdAuthRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Created by tommackenzie on 10/25/15.
 *
 * extending RequestAuthCodeImpl is ok until the optional
 * parameters are implemented in openid.
 */
@Component("requestOpenIdAuthCodeImpl")
public class RequestOpenIdAuthCodeImpl extends RequestAuthCodeImpl {

    private ValidateOpenIdParams validateOpenIdParams;

    public RequestOpenIdAuthCodeImpl() {}

    @Autowired
    public RequestOpenIdAuthCodeImpl(ValidateOpenIdParams validateOpenIdParams, LoginResourceOwner loginResourceOwner, GrantAuthCode grantAuthCode, AuthResponseBuilder authResponseBuilder) {
        this.validateOpenIdParams = validateOpenIdParams;
        this.loginResourceOwner = loginResourceOwner;
        this.grantAuthCode = grantAuthCode;
        this.authResponseBuilder = authResponseBuilder;
    }

    @Override
    public AuthResponse run(AuthCodeInput input) throws UnauthorizedException, InformResourceOwnerException, InformClientException, AuthCodeInsertException {
        OpenIdAuthRequest authRequest = validateOpenIdParams.run(
                input.getClientIds(),
                input.getResponseTypes(),
                input.getRedirectUris(),
                input.getScopes(),
                input.getStates()
        );

        return makeAuthResponse(
                input.getUserName(),
                input.getPlainTextPassword(),
                authRequest.getClientId(),
                Optional.of(authRequest.getRedirectURI()),
                authRequest.getScopes(),
                authRequest.getState()
        );
    }
}
