package org.rootservices.authorization.grant.code.protocol.authorization.response.builder;

import org.rootservices.authorization.constant.ErrorCode;
import org.rootservices.authorization.grant.code.exception.InformResourceOwnerException;
import org.rootservices.authorization.grant.code.protocol.authorization.response.AuthResponse;
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
public class AuthResponseBuilderImpl implements AuthResponseBuilder {

    @Autowired
    private ClientRepository clientRepository;

    public AuthResponseBuilderImpl() {}

    public AuthResponseBuilderImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public AuthResponse run(UUID clientUUID, String authCode, Optional<String> state, Optional<URI> redirectUri) throws InformResourceOwnerException {
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
            client = clientRepository.getByUUID(clientUUID);
        } catch (RecordNotFoundException e) {
            throw new InformResourceOwnerException(
                "Could not make Authorization Response. Client was not found.",
                e, ErrorCode.CLIENT_NOT_FOUND.getCode()
            );

        }
        return client.getRedirectURI();
    }
}
