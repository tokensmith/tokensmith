package org.rootservices.authorization.codegrant.factory;

import org.rootservices.authorization.codegrant.factory.exception.*;
import org.rootservices.authorization.codegrant.factory.optional.RedirectUriFactory;
import org.rootservices.authorization.codegrant.factory.optional.ScopesFactory;
import org.rootservices.authorization.codegrant.factory.required.ClientIdFactory;
import org.rootservices.authorization.codegrant.factory.required.ResponseTypeFactory;
import org.rootservices.authorization.codegrant.request.AuthRequest;
import org.rootservices.authorization.persistence.entity.ResponseType;
import org.rootservices.authorization.persistence.entity.Scope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 2/1/15.
 */
@Component
public class AuthRequestFactoryImpl implements AuthRequestFactory {

    @Autowired
    private ClientIdFactory clientIdFactory;

    @Autowired
    private ResponseTypeFactory responseTypeFactory;

    @Autowired
    private RedirectUriFactory redirectUriFactory;

    @Autowired
    private ScopesFactory scopesFactory;

    public AuthRequestFactoryImpl() {}

    public AuthRequestFactoryImpl(ClientIdFactory clientIdFactory, ResponseTypeFactory responseTypeFactory, RedirectUriFactory redirectUriFactory, ScopesFactory scopesFactory) {
        this.clientIdFactory = clientIdFactory;
        this.responseTypeFactory = responseTypeFactory;
        this.redirectUriFactory = redirectUriFactory;
        this.scopesFactory = scopesFactory;
    }

    @Override
    public AuthRequest makeAuthRequest(List<String> clientIds, List<String> responseTypes, List<String> redirectUris, List<String> scopes) throws ClientIdException, ResponseTypeException, RedirectUriException, ScopesException {

        AuthRequest authRequest = new AuthRequest();

        UUID clientId = clientIdFactory.makeClientId(clientIds);
        authRequest.setClientId(clientId);

        ResponseType responseType = responseTypeFactory.makeResponseType(responseTypes);
        authRequest.setResponseType(responseType);

        Optional<URI> redirectUri = redirectUriFactory.makeRedirectUri(redirectUris);
        authRequest.setRedirectURI(redirectUri);

        List<Scope> cleanedScopes = scopesFactory.makeScopes(scopes);
        authRequest.setScopes(cleanedScopes);

        return authRequest;
    }



}
