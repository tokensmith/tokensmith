package org.rootservices.authorization.codegrant.request;

import org.rootservices.authorization.codegrant.exception.client.InformClientException;
import org.rootservices.authorization.codegrant.exception.client.ResponseTypeIsNotCodeException;
import org.rootservices.authorization.codegrant.exception.client.UnAuthorizedResponseTypeException;
import org.rootservices.authorization.codegrant.exception.resourceowner.ClientNotFoundException;
import org.rootservices.authorization.codegrant.exception.resourceowner.InformResourceOwnerException;
import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.entity.ResponseType;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    public boolean run(AuthRequest authRequest) throws ResponseTypeIsNotCodeException, ClientNotFoundException, UnAuthorizedResponseTypeException {

        isResponseTypeCode(authRequest.getResponseType());
        matchesPersistedClient(authRequest);

        return true;
    }

    private boolean isResponseTypeCode(ResponseType responseType) throws ResponseTypeIsNotCodeException {

        if ( responseType != ResponseType.CODE ) {
            throw new ResponseTypeIsNotCodeException("Response Type was not code");
        }

        return true;
    }

    private boolean matchesPersistedClient(AuthRequest authRequest) throws ClientNotFoundException, UnAuthorizedResponseTypeException {

        Client client;
        try {
            client = clientRepository.getByUUID(authRequest.getClientId());
        } catch (RecordNotFoundException e) {
            throw new ClientNotFoundException("The Client was not found", e);
        }

        if ( client.getResponseType() != authRequest.getResponseType() ) {
            throw new UnAuthorizedResponseTypeException(
                    "Response Type requested doesnt match client's response type",
                    client.getRedirectURI()
            );
        }

        return true;
    }
}
