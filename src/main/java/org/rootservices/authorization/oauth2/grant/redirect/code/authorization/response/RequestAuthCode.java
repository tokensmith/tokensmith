package org.rootservices.authorization.oauth2.grant.redirect.code.authorization.response;

import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.ValidateParams;
import org.rootservices.authorization.authenticate.LoginResourceOwner;
import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.response.entity.InputParams;
import org.rootservices.authorization.oauth2.grant.redirect.code.authorization.response.factory.AuthResponseFactory;
import org.rootservices.authorization.oauth2.grant.redirect.code.authorization.response.exception.AuthCodeInsertException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.entity.AuthRequest;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 4/16/15.
 *
 * Section 4.1.2
 */
@Component
public class RequestAuthCode {

    @Autowired
    private ValidateParams validateParamsCodeGrant;
    @Autowired
    protected LoginResourceOwner loginResourceOwner;
    @Autowired
    protected IssueAuthCode issueAuthCode;
    @Autowired
    protected AuthResponseFactory authResponseFactory;

    public RequestAuthCode() {}

    public RequestAuthCode(ValidateParams validateParamsCodeResponseType, LoginResourceOwner loginResourceOwner, IssueAuthCode issueAuthCode, AuthResponseFactory authResponseFactory) {
        this.validateParamsCodeGrant = validateParamsCodeResponseType;
        this.loginResourceOwner = loginResourceOwner;
        this.issueAuthCode = issueAuthCode;
        this.authResponseFactory = authResponseFactory;
    }

    public AuthResponse run(InputParams input) throws UnauthorizedException, InformResourceOwnerException, InformClientException, AuthCodeInsertException {

        AuthRequest authRequest = validateParamsCodeGrant.run(
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
                authRequest.getRedirectURI(),
                authRequest.getScopes(),
                authRequest.getState()
        );
    }

    protected AuthResponse makeAuthResponse(String userName, String password, UUID clientId, Optional<URI> redirectUri, List<String> scopes, Optional<String> state) throws UnauthorizedException, AuthCodeInsertException, InformResourceOwnerException {

        ResourceOwner resourceOwner = loginResourceOwner.run(userName, password);

        String authorizationCode = issueAuthCode.run(
                resourceOwner.getId(),
                clientId,
                redirectUri,
                scopes
        );

        return authResponseFactory.makeAuthResponse(
                clientId,
                authorizationCode,
                state,
                redirectUri
        );
    }
}
