package org.rootservices.authorization.openId.grant.redirect.code.authorization.request.factory;

import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.exception.*;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.optional.ScopesFactory;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.optional.StateFactory;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.required.ClientIdFactory;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.factory.required.ResponseTypesFactory;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.openId.grant.redirect.code.authorization.request.context.GetOpenIdConfidentialClientRedirectUri;
import org.rootservices.authorization.openId.grant.redirect.shared.authorization.request.factory.required.OpenIdRedirectUriFactory;
import org.rootservices.authorization.openId.grant.redirect.code.authorization.request.entity.OpenIdAuthRequest;
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
public class OpenIdCodeAuthRequestFactory {

    private ClientIdFactory clientIdFactory;
    private OpenIdRedirectUriFactory openIdRedirectUriFactory;
    private ResponseTypesFactory responseTypesFactory;
    private ScopesFactory scopesFactory;
    private StateFactory stateFactory;
    private GetOpenIdConfidentialClientRedirectUri getOpenIdConfidentialClientRedirectUri;

    @Autowired
    public OpenIdCodeAuthRequestFactory(ClientIdFactory clientIdFactory, OpenIdRedirectUriFactory openIdRedirectUriFactory, ResponseTypesFactory responseTypesFactory, ScopesFactory scopesFactory, StateFactory stateFactory, GetOpenIdConfidentialClientRedirectUri getOpenIdConfidentialClientRedirectUri) {
        this.clientIdFactory = clientIdFactory;
        this.openIdRedirectUriFactory = openIdRedirectUriFactory;
        this.responseTypesFactory = responseTypesFactory;
        this.scopesFactory = scopesFactory;
        this.stateFactory = stateFactory;
        this.getOpenIdConfidentialClientRedirectUri = getOpenIdConfidentialClientRedirectUri;
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

        List<String> cleanedResponseType;
        List<String> cleanedScopes;
        Optional<String> cleanedStates;
        try {
            cleanedResponseType = responseTypesFactory.makeResponseTypes(responseTypes);
            cleanedScopes = scopesFactory.makeScopes(scopes);
            cleanedStates = stateFactory.makeState(states);
        } catch (ResponseTypeException |ScopesException | StateException e) {
            getOpenIdConfidentialClientRedirectUri.run(clientId, redirectUri, e);
            throw new InformClientException("", e.getError(), e.getDescription(), e.getCode(), redirectUri, e);
        }

        openIdAuthRequest.setResponseTypes(cleanedResponseType);
        openIdAuthRequest.setScopes(cleanedScopes);
        openIdAuthRequest.setState(cleanedStates);
        return openIdAuthRequest;
    }
}
