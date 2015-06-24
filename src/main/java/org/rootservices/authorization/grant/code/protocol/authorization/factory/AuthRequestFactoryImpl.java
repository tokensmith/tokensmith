package org.rootservices.authorization.grant.code.protocol.authorization.factory;

import org.rootservices.authorization.grant.code.protocol.authorization.request.GetClientRedirect;
import org.rootservices.authorization.grant.code.exception.InformClientException;
import org.rootservices.authorization.grant.code.exception.InformResourceOwnerException;
import org.rootservices.authorization.grant.code.protocol.authorization.factory.exception.*;
import org.rootservices.authorization.grant.code.protocol.authorization.factory.optional.RedirectUriFactory;
import org.rootservices.authorization.grant.code.protocol.authorization.factory.optional.ScopesFactory;
import org.rootservices.authorization.grant.code.protocol.authorization.factory.required.ClientIdFactory;
import org.rootservices.authorization.grant.code.protocol.authorization.factory.required.ResponseTypeFactory;
import org.rootservices.authorization.grant.code.protocol.authorization.request.AuthRequest;
import org.rootservices.authorization.persistence.entity.ResponseType;
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

    @Autowired
    private GetClientRedirect getClientRedirect;

    public AuthRequestFactoryImpl() {}

    public AuthRequestFactoryImpl(ClientIdFactory clientIdFactory, ResponseTypeFactory responseTypeFactory, RedirectUriFactory redirectUriFactory, ScopesFactory scopesFactory, GetClientRedirect getClientRedirect) {
        this.clientIdFactory = clientIdFactory;
        this.responseTypeFactory = responseTypeFactory;
        this.redirectUriFactory = redirectUriFactory;
        this.scopesFactory = scopesFactory;
        this.getClientRedirect = getClientRedirect;
    }

    @Override
    public AuthRequest makeAuthRequest(List<String> clientIds, List<String> responseTypes, List<String> redirectUris, List<String> scopes) throws InformResourceOwnerException, InformClientException {

        AuthRequest authRequest = new AuthRequest();

        UUID clientId;
        Optional<URI> redirectUri;
        try {
            clientId = clientIdFactory.makeClientId(clientIds);
            redirectUri = redirectUriFactory.makeRedirectUri(redirectUris);
        } catch(ClientIdException|RedirectUriException e) {
            throw new InformResourceOwnerException("", e, e.getCode());
        }
        authRequest.setClientId(clientId);
        authRequest.setRedirectURI(redirectUri);

        ResponseType responseType;
        List<String> cleanedScopes;
        try {
            responseType = responseTypeFactory.makeResponseType(responseTypes);
            cleanedScopes = scopesFactory.makeScopes(scopes);
        } catch (ResponseTypeException|ScopesException e) {
            URI clientRedirectURI = getClientRedirect.run(clientId, redirectUri, e);
            throw new InformClientException("", e.getError(), e.getCode(), clientRedirectURI, e);
        }

        authRequest.setResponseType(responseType);
        authRequest.setScopes(cleanedScopes);

        return authRequest;
    }


}
