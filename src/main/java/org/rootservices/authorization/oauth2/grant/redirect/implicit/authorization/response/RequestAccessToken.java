package org.rootservices.authorization.oauth2.grant.redirect.implicit.authorization.response;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.authorization.authenticate.LoginResourceOwner;
import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.exception.ServerException;
import org.rootservices.authorization.oauth2.grant.redirect.implicit.authorization.response.entity.ImplicitAccessToken;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.ValidateParams;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.entity.AuthRequest;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.builder.InformClientExceptionBuilder;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.response.entity.InputParams;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenGraph;
import org.rootservices.authorization.oauth2.grant.token.entity.TokenType;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ClientRepository;
import org.rootservices.authorization.security.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Requests an access token for the token grant flow
 */
@Component
public class RequestAccessToken {
    private static final Logger logger = LogManager.getLogger(RequestAccessToken.class);

    private LoginResourceOwner loginResourceOwner;
    private ValidateParams validateParamsImplicitGrant;
    private IssueTokenImplicitGrant issueTokenImplicitGrant;
    private ClientRepository clientRepository;

    private static String MSG_TOKEN = "Failed to issue token";
    private static String SERVER_ERROR = "server_error";

    @Autowired
    public RequestAccessToken(LoginResourceOwner loginResourceOwner, ValidateParams validateParamsImplicitGrant, IssueTokenImplicitGrant issueTokenImplicitGrant, ClientRepository clientRepository) {
        this.loginResourceOwner = loginResourceOwner;
        this.validateParamsImplicitGrant = validateParamsImplicitGrant;
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
        URI redirectURI = getRedirectURI(authRequest.getRedirectURI(), authRequest.getClientId());
        List<Client> audience = makeAudience(authRequest.getClientId());

        TokenGraph tokenGraph;
        try {
            tokenGraph = issueTokenImplicitGrant.run(
                    authRequest.getClientId(),
                    resourceOwner,
                    authRequest.getScopes(),
                    audience
            );
        } catch (ServerException e) {
            logger.error(e.getMessage(), e);

            ErrorCode ec = ErrorCode.SERVER_ERROR;
            throw new InformClientExceptionBuilder()
                    .setMessage(MSG_TOKEN)
                    .setError(SERVER_ERROR)
                    .setDescription(ec.getDescription())
                    .setErrorCode(ec.getCode())
                    .setRedirectURI(redirectURI)
                    .setState(authRequest.getState())
                    .setCause(e)
                    .build();
        }
        return translate(redirectURI, tokenGraph.getPlainTextAccessToken(), tokenGraph.getToken().getSecondsToExpiration(), authRequest.getScopes(), authRequest.getState());
    }

    private URI getRedirectURI(Optional<URI> requestRedirectURI, UUID clientId) throws InformResourceOwnerException {
        URI redirectUri;
        if (requestRedirectURI.isPresent()) {
            redirectUri = requestRedirectURI.get();
        } else {
            redirectUri = fetchClientRedirectURI(clientId);
        }
        return redirectUri;
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

    private List<Client> makeAudience(UUID clientId) throws InformResourceOwnerException {
        List<Client> audience = new ArrayList<>();

        Client client;
        try {
            client = clientRepository.getById(clientId);
        } catch (RecordNotFoundException e) {
            throw new InformResourceOwnerException(
                    ErrorCode.CLIENT_NOT_FOUND.getDescription(), e, ErrorCode.CLIENT_NOT_FOUND.getCode()
            );
        }
        audience.add(client);
        return audience;
    }

    private ImplicitAccessToken translate(URI redirectUri, String accessToken, Long secondsToExpiration, List<String> scopes, Optional<String> state) {

        Optional<String> scopesForToken = Optional.empty();
        if (scopes != null && scopes.size() > 0) {
            scopesForToken = Optional.of(scopes.stream().map(i -> i.toString()).collect(Collectors.joining(" ")));
        }

        return new ImplicitAccessToken(redirectUri, accessToken, TokenType.BEARER, secondsToExpiration, scopesForToken, state);
    }
}
