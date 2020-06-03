package net.tokensmith.authorization.openId.grant.redirect.code.authorization.response;

import net.tokensmith.authorization.authenticate.CreateLocalToken;
import net.tokensmith.authorization.authenticate.LoginResourceOwner;
import net.tokensmith.authorization.authenticate.exception.LocalSessionException;
import net.tokensmith.authorization.authenticate.exception.UnauthorizedException;
import net.tokensmith.authorization.authenticate.model.Session;
import net.tokensmith.authorization.exception.ServerException;
import net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import net.tokensmith.authorization.oauth2.grant.redirect.code.authorization.response.AuthResponse;
import net.tokensmith.authorization.oauth2.grant.redirect.code.authorization.response.IssueAuthCode;
import net.tokensmith.authorization.oauth2.grant.redirect.code.authorization.response.factory.AuthResponseFactory;
import net.tokensmith.authorization.oauth2.grant.redirect.code.authorization.response.exception.AuthCodeInsertException;
import net.tokensmith.authorization.openId.grant.redirect.code.authorization.request.ValidateOpenIdCodeResponseType;
import net.tokensmith.authorization.openId.grant.redirect.code.authorization.request.entity.OpenIdAuthRequest;
import net.tokensmith.repository.entity.ResourceOwner;
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
    protected CreateLocalToken createLocalToken;

    public RequestOpenIdAuthCode() {}

    @Autowired
    public RequestOpenIdAuthCode(ValidateOpenIdCodeResponseType validateOpenIdCodeResponseType, LoginResourceOwner loginResourceOwner, IssueAuthCode issueAuthCode, CreateLocalToken createLocalToken, AuthResponseFactory authResponseFactory) {
        this.validateOpenIdCodeResponseType = validateOpenIdCodeResponseType;
        this.loginResourceOwner = loginResourceOwner;
        this.issueAuthCode = issueAuthCode;
        this.createLocalToken = createLocalToken;
        this.authResponseFactory = authResponseFactory;
    }

    public AuthResponse run(String userName, String password, Map<String, List<String>> parameters) throws UnauthorizedException, InformResourceOwnerException, InformClientException, ServerException {
        OpenIdAuthRequest authRequest = validateOpenIdCodeResponseType.run(parameters);

        AuthResponse authResponse;
        try {
            authResponse = makeAuthResponse(
                    userName,
                    password,
                    authRequest
            );
        } catch (AuthCodeInsertException | LocalSessionException e) {
            throw new InformClientException(
                    ERROR_MSG, ERROR, ERROR_DESC, authRequest.getRedirectURI(), authRequest.getState(), e
            );
        }
        return authResponse;
    }

    protected AuthResponse makeAuthResponse(String userName, String password, OpenIdAuthRequest authRequest) throws UnauthorizedException, AuthCodeInsertException, InformResourceOwnerException, LocalSessionException {

        ResourceOwner resourceOwner = loginResourceOwner.run(userName, password);

        String authorizationCode = issueAuthCode.run(
                resourceOwner.getId(),
                authRequest.getClientId(),
                Optional.of(authRequest.getRedirectURI()),
                authRequest.getScopes(),
                authRequest.getNonce()
        );

        Session localSession = createLocalToken.makeAndRevokeSession(resourceOwner.getId(), 1);

        return authResponseFactory.makeAuthResponse(
                authRequest.getClientId(),
                authorizationCode,
                authRequest.getState(),
                Optional.of(authRequest.getRedirectURI()),
                localSession.getToken(),
                localSession.getIssuedAt()
        );
    }

}
