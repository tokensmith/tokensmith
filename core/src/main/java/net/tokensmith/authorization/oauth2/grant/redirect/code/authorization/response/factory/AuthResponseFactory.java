package net.tokensmith.authorization.oauth2.grant.redirect.code.authorization.response.factory;

import net.tokensmith.authorization.constant.ErrorCode;
import net.tokensmith.authorization.oauth2.grant.redirect.shared.authorization.request.exception.InformResourceOwnerException;
import net.tokensmith.authorization.oauth2.grant.redirect.code.authorization.response.AuthResponse;
import net.tokensmith.repository.entity.Client;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.repository.repo.ClientRepository;
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

    public AuthResponse makeAuthResponse(UUID clientUUID, String authCode, Optional<String> state, Optional<URI> redirectUri, String sessionToken, Long sessionIssuedAt) throws InformResourceOwnerException {
        AuthResponse authResponse = new AuthResponse();
        authResponse.setCode(authCode);
        authResponse.setState(state);
        authResponse.setSessionToken(sessionToken);
        authResponse.setSessionTokenIssuedAt(sessionIssuedAt);

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
