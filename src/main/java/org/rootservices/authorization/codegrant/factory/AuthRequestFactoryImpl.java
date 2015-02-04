package org.rootservices.authorization.codegrant.factory;

import org.rootservices.authorization.codegrant.exception.client.InformClientException;
import org.rootservices.authorization.codegrant.exception.resourceowner.InformResourceOwnerException;
import org.rootservices.authorization.codegrant.factory.exception.*;
import org.rootservices.authorization.codegrant.factory.optional.RedirectUriFactory;
import org.rootservices.authorization.codegrant.factory.optional.ScopesFactory;
import org.rootservices.authorization.codegrant.factory.required.ClientIdFactory;
import org.rootservices.authorization.codegrant.factory.required.ResponseTypeFactory;
import org.rootservices.authorization.codegrant.request.AuthRequest;
import org.rootservices.authorization.context.GetClientRedirectURI;
import org.rootservices.authorization.persistence.entity.ResponseType;
import org.rootservices.authorization.persistence.entity.Scope;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
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
    private GetClientRedirectURI getClientRedirectURI;

    public AuthRequestFactoryImpl() {}

    public AuthRequestFactoryImpl(ClientIdFactory clientIdFactory, ResponseTypeFactory responseTypeFactory, RedirectUriFactory redirectUriFactory, ScopesFactory scopesFactory, GetClientRedirectURI getClientRedirectURI) {
        this.clientIdFactory = clientIdFactory;
        this.responseTypeFactory = responseTypeFactory;
        this.redirectUriFactory = redirectUriFactory;
        this.scopesFactory = scopesFactory;
        this.getClientRedirectURI = getClientRedirectURI;
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
            throw new InformResourceOwnerException("",e);
        }
        authRequest.setClientId(clientId);
        authRequest.setRedirectURI(redirectUri);

        ResponseType responseType;
        List<Scope> cleanedScopes;
        try {
            responseType = responseTypeFactory.makeResponseType(responseTypes);
            cleanedScopes = scopesFactory.makeScopes(scopes);
        } catch (ResponseTypeException e) {
            URI redirectURI = getRedirectUri(clientId, e);
            throw new InformClientException("", "invalid_request", redirectURI, e);
        } catch (ScopesException e) {
            URI redirectURI = getRedirectUri(clientId, e);
            throw new InformClientException("", e.getError(), redirectURI, e);
        }
        authRequest.setResponseType(responseType);
        authRequest.setScopes(cleanedScopes);

        return authRequest;
    }

    private URI getRedirectUri(UUID clientId, BaseException rte) throws InformResourceOwnerException{
        URI redirectUri = null;
        try {
            redirectUri = getClientRedirectURI.run(clientId);
        }catch(RecordNotFoundException e) {
            // Todo: duplicate causes here?
            throw new InformResourceOwnerException("", rte);
        }
        return redirectUri;
    }



}
