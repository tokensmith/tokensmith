package org.rootservices.authorization.grant.code.protocol.authorization.request.buider;

import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.exception.ClientIdException;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.exception.RedirectUriException;
import org.rootservices.authorization.grant.code.protocol.authorization.request.GetClientRedirect;
import org.rootservices.authorization.grant.code.exception.InformClientException;
import org.rootservices.authorization.grant.code.exception.InformResourceOwnerException;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.optional.RedirectUriBuilder;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.exception.ScopesException;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.optional.ScopesBuilder;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.exception.ResponseTypeException;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.required.ClientIdBuilder;
import org.rootservices.authorization.grant.code.protocol.authorization.request.buider.required.ResponseTypeBuilder;
import org.rootservices.authorization.grant.code.protocol.authorization.request.entity.AuthRequest;
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
public class AuthRequestBuilderImpl implements AuthRequestBuilder {

    @Autowired
    private ClientIdBuilder clientIdBuilder;

    @Autowired
    private ResponseTypeBuilder responseTypeBuilder;

    @Autowired
    private RedirectUriBuilder redirectUriBuilder;

    @Autowired
    private ScopesBuilder scopesBuilder;

    @Autowired
    private GetClientRedirect getClientRedirect;

    public AuthRequestBuilderImpl() {}

    public AuthRequestBuilderImpl(ClientIdBuilder clientIdBuilder, ResponseTypeBuilder responseTypeBuilder, RedirectUriBuilder redirectUriBuilder, ScopesBuilder scopesBuilder, GetClientRedirect getClientRedirect) {
        this.clientIdBuilder = clientIdBuilder;
        this.responseTypeBuilder = responseTypeBuilder;
        this.redirectUriBuilder = redirectUriBuilder;
        this.scopesBuilder = scopesBuilder;
        this.getClientRedirect = getClientRedirect;
    }

    @Override
    public AuthRequest makeAuthRequest(List<String> clientIds, List<String> responseTypes, List<String> redirectUris, List<String> scopes) throws InformResourceOwnerException, InformClientException {

        AuthRequest authRequest = new AuthRequest();

        UUID clientId;
        Optional<URI> redirectUri;
        try {
            clientId = clientIdBuilder.makeClientId(clientIds);
            redirectUri = redirectUriBuilder.makeRedirectUri(redirectUris);
        } catch(ClientIdException |RedirectUriException e) {
            throw new InformResourceOwnerException("", e, e.getCode());
        }
        authRequest.setClientId(clientId);
        authRequest.setRedirectURI(redirectUri);

        ResponseType responseType;
        List<String> cleanedScopes;
        try {
            responseType = responseTypeBuilder.makeResponseType(responseTypes);
            cleanedScopes = scopesBuilder.makeScopes(scopes);
        } catch (ResponseTypeException |ScopesException e) {
            URI clientRedirectURI = getClientRedirect.run(clientId, redirectUri, e);
            throw new InformClientException("", e.getError(), e.getCode(), clientRedirectURI, e);
        }

        authRequest.setResponseType(responseType);
        authRequest.setScopes(cleanedScopes);

        return authRequest;
    }


}
