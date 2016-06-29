package org.rootservices.authorization.oauth2.grant.redirect.token.authorization.response;

import org.rootservices.authorization.authenticate.LoginResourceOwner;
import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.ValidateParams;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.entity.AuthRequest;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.response.entity.GrantInput;
import org.rootservices.authorization.oauth2.grant.redirect.code.token.response.TokenType;
import org.rootservices.authorization.oauth2.grant.redirect.token.authorization.response.dto.TokenGrantAccessToken;
import org.rootservices.authorization.persistence.entity.*;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ClientRepository;
import org.rootservices.authorization.persistence.repository.ScopeRepository;
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
    private LoginResourceOwner loginResourceOwner;
    private ValidateParams validateParamsTokenResponseType;
    private ScopeRepository scopeRepository;
    private RandomString randomString;
    private GrantToken grantToken;
    private ClientRepository clientRepository;

    @Autowired
    public RequestAccessToken(LoginResourceOwner loginResourceOwner, ValidateParams validateParamsTokenResponseType, ScopeRepository scopeRepository, RandomString randomString, GrantToken grantToken, ClientRepository clientRepository) {
        this.loginResourceOwner = loginResourceOwner;
        this.validateParamsTokenResponseType = validateParamsTokenResponseType;
        this.scopeRepository = scopeRepository;
        this.randomString = randomString;
        this.grantToken = grantToken;
        this.clientRepository = clientRepository;
    }

    public TokenGrantAccessToken requestToken(GrantInput grantInput) throws InformClientException, InformResourceOwnerException, UnauthorizedException {

        AuthRequest authRequest = validateParamsTokenResponseType.run(
                grantInput.getClientIds(),
                grantInput.getResponseTypes(),
                grantInput.getRedirectUris(),
                grantInput.getScopes(),
                grantInput.getStates()
        );
        ResourceOwner resourceOwner = loginResourceOwner.run(grantInput.getUserName(), grantInput.getPlainTextPassword());

        String accessToken = randomString.run();

        List<Scope> scopesForToken = scopeRepository.findByName(authRequest.getScopes());
        Token token = grantToken.grant(resourceOwner, scopesForToken, accessToken);

        URI redirectUri;
        if (authRequest.getRedirectURI().isPresent()) {
            redirectUri = authRequest.getRedirectURI().get();
        } else {
            redirectUri = fetchClientRedirectURI(authRequest.getClientId());
        }
        return translate(redirectUri, accessToken, grantToken.getSecondsToExpiration(), authRequest.getScopes(), authRequest.getState());
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
    private TokenGrantAccessToken translate(URI redirectUri, String accessToken, Long secondsToExpiration, List<String> scopes, Optional<String> state) {

        Optional<String> scopesForToken = Optional.empty();
        if (scopes != null && scopes.size() > 0) {
            scopesForToken = Optional.of(scopes.stream().map(i -> i.toString()).collect(Collectors.joining(" ")));
        }

        return new TokenGrantAccessToken(redirectUri, accessToken, TokenType.BEARER, secondsToExpiration, scopesForToken, state);
    }
}
