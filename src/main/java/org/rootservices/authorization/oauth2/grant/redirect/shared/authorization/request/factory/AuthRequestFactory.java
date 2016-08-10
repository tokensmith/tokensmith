package org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory;

import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.exception.*;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.optional.RedirectUriFactory;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.optional.ScopesFactory;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.optional.StateFactory;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.required.ClientIdFactory;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.required.ResponseTypesFactory;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.context.GetClientRedirectUri;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.entity.AuthRequest;
import org.rootservices.authorization.persistence.entity.ResponseType;


import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 2/1/15.
 */
public class AuthRequestFactory {

    private ClientIdFactory clientIdFactory;
    private ResponseTypesFactory responseTypesFactory;
    private RedirectUriFactory redirectUriFactory;
    private ScopesFactory scopesFactory;
    private StateFactory stateFactory;
    private GetClientRedirectUri getClientRedirect;

    public AuthRequestFactory() {}

    public AuthRequestFactory(ClientIdFactory clientIdFactory, ResponseTypesFactory responseTypesFactory, RedirectUriFactory redirectUriFactory, ScopesFactory scopesFactory, StateFactory stateFactory, GetClientRedirectUri getClientRedirect) {
        this.clientIdFactory = clientIdFactory;
        this.responseTypesFactory = responseTypesFactory;
        this.redirectUriFactory = redirectUriFactory;
        this.scopesFactory = scopesFactory;
        this.stateFactory = stateFactory;
        this.getClientRedirect = getClientRedirect;
    }

    public AuthRequest makeAuthRequest(List<String> clientIds, List<String> responseTypes, List<String> redirectUris, List<String> scopes, List<String> states) throws InformResourceOwnerException, InformClientException {

        AuthRequest authRequest = new AuthRequest();

        UUID clientId;
        Optional<URI> redirectUri;
        try {
            clientId = clientIdFactory.makeClientId(clientIds);
            redirectUri = redirectUriFactory.makeRedirectUri(redirectUris);
        } catch(ClientIdException |RedirectUriException e) {
            throw new InformResourceOwnerException("", e, e.getCode());
        }
        authRequest.setClientId(clientId);
        authRequest.setRedirectURI(redirectUri);

        List<String> cleanedResponseTypes;
        List<String> cleanedScopes;
        Optional<String> cleanedStates;
        try {
            cleanedResponseTypes = responseTypesFactory.makeResponseTypes(responseTypes);
            cleanedScopes = scopesFactory.makeScopes(scopes);
            cleanedStates = stateFactory.makeState(states);
        } catch (ResponseTypeException |ScopesException | StateException e) {
            URI clientRedirectUri = getClientRedirect.run(clientId, redirectUri, e);
            throw new InformClientException("", e.getError(), e.getDescription(), e.getCode(), clientRedirectUri, e);
        }

        authRequest.setResponseTypes(cleanedResponseTypes);
        authRequest.setScopes(cleanedScopes);
        authRequest.setState(cleanedStates);

        return authRequest;
    }
}
