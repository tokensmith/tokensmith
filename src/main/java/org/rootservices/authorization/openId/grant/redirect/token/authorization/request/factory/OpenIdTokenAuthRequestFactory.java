package org.rootservices.authorization.openId.grant.redirect.token.authorization.request.factory;

import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.exception.*;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.optional.ScopesFactory;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.optional.StateFactory;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.required.ClientIdFactory;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.required.ResponseTypesFactory;
import org.rootservices.authorization.openId.grant.redirect.shared.authorization.request.context.GetOpenIdClientRedirectUri;
import org.rootservices.authorization.openId.grant.redirect.shared.authorization.request.factory.required.OpenIdRedirectUriFactory;
import org.rootservices.authorization.openId.grant.redirect.token.authorization.request.context.GetOpenIdPublicClientRedirectUri;
import org.rootservices.authorization.openId.grant.redirect.token.authorization.request.entity.OpenIdTokenAuthRequest;
import org.rootservices.authorization.openId.grant.redirect.token.authorization.request.factory.exception.NonceException;
import org.rootservices.authorization.openId.grant.redirect.token.authorization.request.factory.required.NonceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class OpenIdTokenAuthRequestFactory {

    private ClientIdFactory clientIdFactory;
    private OpenIdRedirectUriFactory openIdRedirectUriFactory;
    private ResponseTypesFactory responseTypesFactory;
    private ScopesFactory scopesFactory;
    private StateFactory stateFactory;
    private NonceFactory nonceFactory;
    private GetOpenIdPublicClientRedirectUri getOpenIdPublicClientRedirectUri;

    @Autowired
    public OpenIdTokenAuthRequestFactory(ClientIdFactory clientIdFactory, OpenIdRedirectUriFactory openIdRedirectUriFactory, ResponseTypesFactory responseTypesFactory, ScopesFactory scopesFactory, StateFactory stateFactory, NonceFactory nonceFactory, GetOpenIdPublicClientRedirectUri getOpenIdPublicClientRedirectUri) {
        this.clientIdFactory = clientIdFactory;
        this.openIdRedirectUriFactory = openIdRedirectUriFactory;
        this.responseTypesFactory = responseTypesFactory;
        this.scopesFactory = scopesFactory;
        this.stateFactory = stateFactory;
        this.nonceFactory = nonceFactory;
        this.getOpenIdPublicClientRedirectUri = getOpenIdPublicClientRedirectUri;
    }

    public OpenIdTokenAuthRequest make(List<String> clientIds, List<String> responseTypes, List<String> redirectUris, List<String> scopes, List<String> states, List<String> nonces) throws InformResourceOwnerException, InformClientException {
        OpenIdTokenAuthRequest authRequest = new OpenIdTokenAuthRequest();

        UUID clientId;
        URI redirectUri;
        try {
            clientId = clientIdFactory.makeClientId(clientIds);
            redirectUri = openIdRedirectUriFactory.makeRedirectUri(redirectUris);
        }catch(ClientIdException |RedirectUriException e) {
            throw new InformResourceOwnerException("", e, e.getCode());
        }

        authRequest.setClientId(clientId);
        authRequest.setRedirectURI(redirectUri);

        List<String> cleanedResponseType;
        List<String> cleanedScopes;
        Optional<String> cleanedStates;
        String nonce;

        try {
            cleanedResponseType = responseTypesFactory.makeResponseTypes(responseTypes);
            cleanedScopes = scopesFactory.makeScopes(scopes);
            cleanedStates = stateFactory.makeState(states);
            nonce = nonceFactory.makeNonce(nonces);
        } catch (ResponseTypeException |ScopesException | StateException | NonceException e) {
            getOpenIdPublicClientRedirectUri.run(clientId, redirectUri, e);
            throw new InformClientException("", e.getError(), e.getDescription(), e.getCode(), redirectUri, e);
        }

        authRequest.setResponseTypes(cleanedResponseType);
        authRequest.setScopes(cleanedScopes);
        authRequest.setState(cleanedStates);
        authRequest.setNonce(nonce);
        return authRequest;
    }
}
