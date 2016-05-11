package org.rootservices.authorization.oauth2.grant.code.authorization.request.context;

import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.code.authorization.request.exception.InformClientException;
import org.rootservices.authorization.oauth2.grant.code.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.persistence.entity.ConfidentialClient;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ConfidentialClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 2/18/15.
 */
@Component
public class GetClientRedirectUriImpl implements GetClientRedirectUri {

    private ConfidentialClientRepository confidentialClientRepository;

    @Autowired
    public GetClientRedirectUriImpl(ConfidentialClientRepository confidentialClientRepository) {
        this.confidentialClientRepository = confidentialClientRepository;
    }

    @Override
    public URI run(UUID clientId, Optional<URI> redirectURI, Throwable rootCause) throws InformClientException, InformResourceOwnerException {

        ConfidentialClient confidentialClient;
        try {
            confidentialClient = confidentialClientRepository.getByClientId(clientId);
        } catch (RecordNotFoundException e) {
            throw new InformResourceOwnerException("", e, ErrorCode.CLIENT_NOT_FOUND.getCode());
        }

        if ( redirectMismatch(redirectURI, confidentialClient.getClient().getRedirectURI())) {
            throw new InformResourceOwnerException("", rootCause, ErrorCode.REDIRECT_URI_MISMATCH.getCode());
        }

        return confidentialClient.getClient().getRedirectURI();
    }

    /*
    returns true if the redirect does not match client's redirect, otherwise
    returns false
     */
    private boolean redirectMismatch(Optional<URI> redirect, URI clientRedirect) {
        boolean matches = false;
        if ( redirect.isPresent()) {
            matches = !clientRedirect.equals(redirect.get());
        }
        return matches;
    }
}
