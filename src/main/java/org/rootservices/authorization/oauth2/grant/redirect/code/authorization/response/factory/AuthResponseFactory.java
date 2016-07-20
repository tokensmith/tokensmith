package org.rootservices.authorization.oauth2.grant.redirect.code.authorization.response.factory;

import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.oauth2.grant.redirect.authorization.request.exception.InformResourceOwnerException;
import org.rootservices.authorization.oauth2.grant.redirect.code.authorization.response.AuthResponse;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 4/29/15.
 */
@Component
public class AuthResponseFactory {

    @Autowired
    private ClientRepository clientRepository;

    public AuthResponseFactory() {}

    public AuthResponseFactory(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public AuthResponse makeAuthResponse(UUID clientUUID, String authCode, Optional<String> state, Optional<URI> redirectUri) throws InformResourceOwnerException {
        AuthResponse authResponse = new AuthResponse();
        authResponse.setCode(authCode);
        authResponse.setState(state);

        if (redirectUri.isPresent()) {
            authResponse.setRedirectUri(redirectUri.get());
        } else {
            authResponse.setRedirectUri(
                getRedirectUriFromPersistence(clientUUID)
            );
        }

        return authResponse;
    }

    private URI getRedirectUriFromPersistence(UUID clientUUID) throws InformResourceOwnerException {
        Client client = null;
        try {
            client = clientRepository.getById(clientUUID);
        } catch (RecordNotFoundException e) {
            throw new InformResourceOwnerException(
                "Could not make Authorization Response. Client was not found.",
                e, ErrorCode.CLIENT_NOT_FOUND.getCode()
            );

        }
        return client.getRedirectURI();
    }
}
