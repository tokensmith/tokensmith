package org.rootservices.authorization.openId.grant.redirect.code.authorization.response;

import org.rootservices.authorization.authenticate.LoginResourceOwner;
import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.response.entity.GrantInput;
import org.rootservices.authorization.oauth2.grant.redirect.code.authorization.response.AuthResponse;
import org.rootservices.authorization.oauth2.grant.redirect.code.authorization.response.GrantAuthCode;
import org.rootservices.authorization.oauth2.grant.redirect.code.authorization.response.RequestAuthCodeImpl;
import org.rootservices.authorization.oauth2.grant.redirect.code.authorization.response.factory.AuthResponseFactory;
import org.rootservices.authorization.oauth2.grant.redirect.code.authorization.response.exception.AuthCodeInsertException;
import org.rootservices.authorization.openId.grant.redirect.code.authorization.request.ValidateOpenIdCodeResponseType;
import org.rootservices.authorization.openId.grant.redirect.shared.authorization.request.entity.OpenIdAuthRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Created by tommackenzie on 10/25/15.
 *
 * extending RequestAuthCodeImpl is ok until the optional
 * parameters are implemented in openid.
 */
@Component("requestOpenIdAuthCode")
public class RequestOpenIdAuthCodeImpl extends RequestAuthCodeImpl {

    private ValidateOpenIdCodeResponseType validateOpenIdCodeResponseType;

    public RequestOpenIdAuthCodeImpl() {}

    @Autowired
    public RequestOpenIdAuthCodeImpl(ValidateOpenIdCodeResponseType validateOpenIdCodeResponseType, LoginResourceOwner loginResourceOwner, GrantAuthCode grantAuthCode, AuthResponseFactory authResponseFactory) {
        this.validateOpenIdCodeResponseType = validateOpenIdCodeResponseType;
        this.loginResourceOwner = loginResourceOwner;
        this.grantAuthCode = grantAuthCode;
        this.authResponseFactory = authResponseFactory;
    }

    @Override
    public AuthResponse run(GrantInput input) throws UnauthorizedException, InformResourceOwnerException, InformClientException, AuthCodeInsertException {
        OpenIdAuthRequest authRequest = validateOpenIdCodeResponseType.run(
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
