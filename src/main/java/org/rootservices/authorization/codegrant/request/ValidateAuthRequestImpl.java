package org.rootservices.authorization.codegrant.request;

import org.rootservices.authorization.codegrant.exception.client.InformClientException;
import org.rootservices.authorization.codegrant.exception.client.MissingResponseTypeException;
import org.rootservices.authorization.codegrant.exception.client.ResponseTypeIsNotCodeException;
import org.rootservices.authorization.codegrant.exception.resourceowner.ClientNotFoundException;
import org.rootservices.authorization.codegrant.exception.resourceowner.InformResourceOwnerException;
import org.rootservices.authorization.codegrant.exception.resourceowner.MissingClientIdException;
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

    public boolean run(AuthRequest authRequest) throws InformResourceOwnerException, InformClientException {

        hasRequiredFields(authRequest);
        isResponseTypeCode(authRequest.getResponseType());
        matchesPersistedClient(authRequest);

        return true;
    }

    private boolean hasRequiredFields(AuthRequest authRequest) throws InformResourceOwnerException, InformClientException {

        if ( authRequest.getClientId() == null ) {
            throw new MissingClientIdException("Client Id is missing");
        }

        if ( authRequest.getResponseType() == null ) {
            throw new MissingResponseTypeException("Response Type is missing");
        }

        return true;
    }

    private boolean isResponseTypeCode(ResponseType responseType) throws InformClientException {

        if ( responseType != ResponseType.CODE ) {
            throw new ResponseTypeIsNotCodeException("Response Type was not code");
        }

        return true;
    }

    private boolean matchesPersistedClient(AuthRequest authRequest) throws InformResourceOwnerException {

        Client client;
        try {
            client = clientRepository.getByUUID(authRequest.getClientId());
        } catch (RecordNotFoundException e) {
            throw new ClientNotFoundException("The Client was not found", e);
        }

        return true;
    }
}
