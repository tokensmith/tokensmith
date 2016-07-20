package org.rootservices.authorization.openId.grant.code.authorization.request.factory;

import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.factory.exception.*;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.factory.optional.ScopesFactory;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.factory.optional.StateFactory;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.factory.required.ClientIdFactory;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.factory.required.ResponseTypeFactory;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.openId.grant.code.authorization.request.CompareClientRedirectUri;
import org.rootservices.authorization.openId.grant.code.authorization.request.factory.required.OpenIdRedirectUriFactory;
import org.rootservices.authorization.openId.grant.code.authorization.request.entity.OpenIdAuthRequest;
import org.rootservices.authorization.persistence.entity.ResponseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 10/1/15.
 */
@Component
public class OpenIdAuthRequestFactory {

    private ClientIdFactory clientIdFactory;
    private OpenIdRedirectUriFactory openIdRedirectUriFactory;
    private ResponseTypeFactory responseTypeFactory;
    private ScopesFactory scopesFactory;
    private StateFactory stateFactory;
    private CompareClientRedirectUri compareClientRedirectUri;

    @Autowired
    public OpenIdAuthRequestFactory(ClientIdFactory clientIdFactory, OpenIdRedirectUriFactory openIdRedirectUriFactory, ResponseTypeFactory responseTypeFactory, ScopesFactory scopesFactory, StateFactory stateFactory, CompareClientRedirectUri compareClientRedirectUri) {
        this.clientIdFactory = clientIdFactory;
        this.openIdRedirectUriFactory = openIdRedirectUriFactory;
        this.responseTypeFactory = responseTypeFactory;
        this.scopesFactory = scopesFactory;
        this.stateFactory = stateFactory;
        this.compareClientRedirectUri = compareClientRedirectUri;
    }

    public OpenIdAuthRequest make(List<String> clientIds, List<String> responseTypes, List<String> redirectUris, List<String> scopes, List<String> states) throws InformResourceOwnerException, InformClientException {
        OpenIdAuthRequest openIdAuthRequest = new OpenIdAuthRequest();

        UUID clientId;
        URI redirectUri;
        try {
            clientId = clientIdFactory.makeClientId(clientIds);
            redirectUri = openIdRedirectUriFactory.makeRedirectUri(redirectUris);
        }catch(ClientIdException |RedirectUriException e) {
            throw new InformResourceOwnerException("", e, e.getCode());
        }

        openIdAuthRequest.setClientId(clientId);
        openIdAuthRequest.setRedirectURI(redirectUri);

        ResponseType responseType;
        List<String> cleanedScopes;
        Optional<String> cleanedStates;
        try {
            responseType = responseTypeFactory.makeResponseType(responseTypes);
            cleanedScopes = scopesFactory.makeScopes(scopes);
            cleanedStates = stateFactory.makeState(states);
        } catch (ResponseTypeException |ScopesException | StateException e) {
            compareClientRedirectUri.run(clientId, redirectUri, e);
            throw new InformClientException("", e.getError(), e.getDescription(), e.getCode(), redirectUri, e);
        }

        openIdAuthRequest.setResponseType(responseType);
        openIdAuthRequest.setScopes(cleanedScopes);
        openIdAuthRequest.setState(cleanedStates);
        return openIdAuthRequest;
    }
}
