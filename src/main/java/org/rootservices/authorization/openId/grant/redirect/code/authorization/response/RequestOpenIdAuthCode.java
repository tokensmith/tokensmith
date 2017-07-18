package org.rootservices.authorization.openId.grant.redirect.code.authorization.response;

import org.rootservices.authorization.authenticate.LoginResourceOwner;
import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.exception.ServerException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.oauth2.grant.redirect.code.authorization.response.AuthResponse;
import org.rootservices.authorization.oauth2.grant.redirect.code.authorization.response.IssueAuthCode;
import org.rootservices.authorization.oauth2.grant.redirect.code.authorization.response.factory.AuthResponseFactory;
import org.rootservices.authorization.oauth2.grant.redirect.code.authorization.response.exception.AuthCodeInsertException;
import org.rootservices.authorization.openId.grant.redirect.code.authorization.request.ValidateOpenIdCodeResponseType;
import org.rootservices.authorization.openId.grant.redirect.code.authorization.request.entity.OpenIdAuthRequest;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@Component
public class RequestOpenIdAuthCode {
    private static String ERROR_MSG = "failed to issue authorization code";
    private static String ERROR = "server_error";
    private static String ERROR_DESC = "failed to issue authorization code";

    private ValidateOpenIdCodeResponseType validateOpenIdCodeResponseType;
    protected LoginResourceOwner loginResourceOwner;
    protected IssueAuthCode issueAuthCode;
    protected AuthResponseFactory authResponseFactory;

    public RequestOpenIdAuthCode() {}

    @Autowired
    public RequestOpenIdAuthCode(ValidateOpenIdCodeResponseType validateOpenIdCodeResponseType, LoginResourceOwner loginResourceOwner, IssueAuthCode issueAuthCode, AuthResponseFactory authResponseFactory) {
        this.validateOpenIdCodeResponseType = validateOpenIdCodeResponseType;
        this.loginResourceOwner = loginResourceOwner;
        this.issueAuthCode = issueAuthCode;
        this.authResponseFactory = authResponseFactory;
    }

    public AuthResponse run(String userName, String password, Map<String, List<String>> parameters) throws UnauthorizedException, InformResourceOwnerException, InformClientException, ServerException {
        OpenIdAuthRequest authRequest = validateOpenIdCodeResponseType.run(parameters);

        AuthResponse authResponse;
        try {
            authResponse = makeAuthResponse(
                    userName,
                    password,
                    authRequest.getClientId(),
                    Optional.of(authRequest.getRedirectURI()),
                    authRequest.getScopes(),
                    authRequest.getState()
            );
        } catch (AuthCodeInsertException e) {
            throw new InformClientException(
                    ERROR_MSG, ERROR, ERROR_DESC, authRequest.getRedirectURI(), authRequest.getState(), e
            );
        }
        return authResponse;
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
