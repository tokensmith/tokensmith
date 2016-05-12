package org.rootservices.authorization.openId.grant.code.authorization.request.builder;

import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.exception.*;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.openId.grant.code.authorization.request.CompareClientRedirectUri;
import org.rootservices.authorization.openId.grant.code.authorization.request.builder.required.OpenIdRedirectUriBuilder;
import org.rootservices.authorization.openId.grant.code.authorization.request.entity.OpenIdAuthRequest;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.optional.ScopesBuilder;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.optional.StateBuilder;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.required.ClientIdBuilder;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.buider.required.ResponseTypeBuilder;
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
public class OpenIdAuthRequestBuilderImpl implements OpenIdAuthRequestBuilder{

    private ClientIdBuilder clientIdBuilder;
    private OpenIdRedirectUriBuilder openIdRedirectUriBuilder;
    private ResponseTypeBuilder responseTypeBuilder;
    private ScopesBuilder scopesBuilder;
    private StateBuilder stateBuilder;
    private CompareClientRedirectUri compareClientRedirectUri;

    @Autowired
    public OpenIdAuthRequestBuilderImpl(ClientIdBuilder clientIdBuilder, OpenIdRedirectUriBuilder openIdRedirectUriBuilder, ResponseTypeBuilder responseTypeBuilder, ScopesBuilder scopesBuilder, StateBuilder stateBuilder, CompareClientRedirectUri compareClientRedirectUri) {
        this.clientIdBuilder = clientIdBuilder;
        this.openIdRedirectUriBuilder = openIdRedirectUriBuilder;
        this.responseTypeBuilder = responseTypeBuilder;
        this.scopesBuilder = scopesBuilder;
        this.stateBuilder = stateBuilder;
        this.compareClientRedirectUri = compareClientRedirectUri;
    }

    @Override
    public OpenIdAuthRequest build(List<String> clientIds, List<String> responseTypes, List<String> redirectUris, List<String> scopes, List<String> states) throws InformResourceOwnerException, InformClientException {
        OpenIdAuthRequest openIdAuthRequest = new OpenIdAuthRequest();

        UUID clientId;
        URI redirectUri;
        try {
            clientId = clientIdBuilder.makeClientId(clientIds);
            redirectUri = openIdRedirectUriBuilder.build(redirectUris);
        }catch(ClientIdException |RedirectUriException e) {
            throw new InformResourceOwnerException("", e, e.getCode());
        }

        openIdAuthRequest.setClientId(clientId);
        openIdAuthRequest.setRedirectURI(redirectUri);

        ResponseType responseType;
        List<String> cleanedScopes;
        Optional<String> cleanedStates;
        try {
            responseType = responseTypeBuilder.makeResponseType(responseTypes);
            cleanedScopes = scopesBuilder.makeScopes(scopes);
            cleanedStates = stateBuilder.makeState(states);
        } catch (ResponseTypeException |ScopesException | StateException e) {
            compareClientRedirectUri.run(clientId, redirectUri, e);
            throw new InformClientException("", e.getError(), e.getCode(), redirectUri, e);
        }

        openIdAuthRequest.setResponseType(responseType);
        openIdAuthRequest.setScopes(cleanedScopes);
        openIdAuthRequest.setState(cleanedStates);
        return openIdAuthRequest;
    }
}
