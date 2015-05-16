package org.rootservices.authorization.grant.code.request;

import org.rootservices.authorization.grant.code.constant.ErrorCode;
import org.rootservices.authorization.grant.code.exception.InformClientException;
import org.rootservices.authorization.grant.code.exception.InformResourceOwnerException;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.Scope;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * Created by tommackenzie on 11/19/14.
 */
@Component
public class ValidateAuthRequestImpl implements ValidateAuthRequest {

    @Autowired
    private ClientRepository clientRepository;

    public ValidateAuthRequestImpl() {}

    public ValidateAuthRequestImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public boolean run(AuthRequest authRequest) throws InformResourceOwnerException, InformClientException {

        Client client;
        try {
            client = clientRepository.getByUUID(authRequest.getClientId());
        } catch (RecordNotFoundException e) {
            throw new InformResourceOwnerException("The Client was not found", e, ErrorCode.CLIENT_NOT_FOUND.getCode());
        }

        if ( redirectMismatch(authRequest.getRedirectURI(), client.getRedirectURI()) ) {
            throw new InformResourceOwnerException(
                    "Redirect URI requested doesn't match client's redirect uri",
                    ErrorCode.REDIRECT_URI_MISMATCH.getCode()
            );
        }

        if ( client.getResponseType() != authRequest.getResponseType() ) {
            throw new InformClientException(
                    "Response Type requested doesn't match client's response type",
                    "unauthorized_client",
                    ErrorCode.RESPONSE_TYPE_MISMATCH.getCode(),
                    client.getRedirectURI()
            );
        }

        if (! hasScopes(authRequest.getScopes(), client.getScopes())) {
            throw new InformClientException(
                    "Scope is not supported for this client.",
                    "invalid_scope",
                    ErrorCode.SCOPES_NOT_SUPPORTED.getCode(),
                    client.getRedirectURI()
            );
        }
        return true;
    }

    private boolean hasScopes(List<String> requestedScopes, List<Scope> clientScopes) {
        boolean hasScopes = true;
        for(String scope: requestedScopes) {
            if (! clientScopes.stream().filter(o -> o.getName().equals(scope)).findFirst().isPresent()) {
                hasScopes = false;
                break;
            }
        }
        return hasScopes;
    }
    /**
     * returns true if the redirect does not match client's redirect,
     * otherwise returns false
     */
    private boolean redirectMismatch(Optional<URI> redirect, URI clientRedirect) {
        boolean matches = false;
        if ( redirect.isPresent()) {
            matches = !clientRedirect.equals(redirect.get());
        }
        return matches;
    }
}
