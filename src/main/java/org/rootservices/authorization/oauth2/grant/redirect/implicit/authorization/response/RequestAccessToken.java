package org.rootservices.authorization.oauth2.grant.redirect.implicit.authorization.response;

import org.rootservices.authorization.authenticate.LoginResourceOwner;
import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.implicit.authorization.response.entity.ImplicitAccessToken;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.ValidateParams;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.entity.AuthRequest;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.response.entity.InputParams;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.response.TokenType;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ClientRepository;
import org.rootservices.authorization.security.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Requests an access token for the token grant flow
 */
@Component
public class RequestAccessToken {
    @Autowired
    private LoginResourceOwner loginResourceOwner;
    @Autowired
    private ValidateParams validateParamsImplicitGrant;
    @Autowired
    private RandomString randomString;
    @Autowired
    private IssueTokenImplicitGrant issueTokenImplicitGrant;
    @Autowired
    private ClientRepository clientRepository;

    public RequestAccessToken() {
    }

    public RequestAccessToken(LoginResourceOwner loginResourceOwner, ValidateParams validateParamsImplicitGrant, RandomString randomString, IssueTokenImplicitGrant issueTokenImplicitGrant, ClientRepository clientRepository) {
        this.loginResourceOwner = loginResourceOwner;
        this.validateParamsImplicitGrant = validateParamsImplicitGrant;
        this.randomString = randomString;
        this.issueTokenImplicitGrant = issueTokenImplicitGrant;
        this.clientRepository = clientRepository;
    }

    public ImplicitAccessToken requestToken(InputParams inputParams) throws InformClientException, InformResourceOwnerException, UnauthorizedException {

        AuthRequest authRequest = validateParamsImplicitGrant.run(
                inputParams.getClientIds(),
                inputParams.getResponseTypes(),
                inputParams.getRedirectUris(),
                inputParams.getScopes(),
                inputParams.getStates()
        );
        ResourceOwner resourceOwner = loginResourceOwner.run(inputParams.getUserName(), inputParams.getPlainTextPassword());

        String accessToken = randomString.run();
        Token token = issueTokenImplicitGrant.run(resourceOwner, authRequest.getScopes(), accessToken);

        URI redirectUri;
        if (authRequest.getRedirectURI().isPresent()) {
            redirectUri = authRequest.getRedirectURI().get();
        } else {
            redirectUri = fetchClientRedirectURI(authRequest.getClientId());
        }
        return translate(redirectUri, accessToken, issueTokenImplicitGrant.getSecondsToExpiration(), authRequest.getScopes(), authRequest.getState());
    }

    private URI fetchClientRedirectURI(UUID clientId) throws InformResourceOwnerException {

        try {
            Client client = clientRepository.getById(clientId);
            return client.getRedirectURI();
        } catch (RecordNotFoundException e) {
            throw new InformResourceOwnerException(
                    ErrorCode.CLIENT_NOT_FOUND.getDescription(), e, ErrorCode.CLIENT_NOT_FOUND.getCode()
            );
        }
    }
    private ImplicitAccessToken translate(URI redirectUri, String accessToken, Long secondsToExpiration, List<String> scopes, Optional<String> state) {

        Optional<String> scopesForToken = Optional.empty();
        if (scopes != null && scopes.size() > 0) {
            scopesForToken = Optional.of(scopes.stream().map(i -> i.toString()).collect(Collectors.joining(" ")));
        }

        return new ImplicitAccessToken(redirectUri, accessToken, TokenType.BEARER, secondsToExpiration, scopesForToken, state);
    }
}
