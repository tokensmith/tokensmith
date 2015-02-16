package org.rootservices.authorization.codegrant.request;

import org.rootservices.authorization.codegrant.constant.ErrorCode;
import org.rootservices.authorization.codegrant.exception.client.ResponseTypeIsNotCodeException;
import org.rootservices.authorization.codegrant.exception.client.UnAuthorizedResponseTypeException;
import org.rootservices.authorization.codegrant.exception.resourceowner.ClientNotFoundException;
import org.rootservices.authorization.codegrant.exception.resourceowner.RedirectUriMismatchException;
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

    public boolean run(AuthRequest authRequest) throws ResponseTypeIsNotCodeException, ClientNotFoundException, UnAuthorizedResponseTypeException, RedirectUriMismatchException{

        isResponseTypeCode(authRequest.getResponseType());
        matchesPersistedClient(authRequest);

        return true;
    }

    private boolean isResponseTypeCode(ResponseType responseType) throws ResponseTypeIsNotCodeException {

        if ( responseType != ResponseType.CODE ) {
            throw new ResponseTypeIsNotCodeException(
                    "Response Type was not code",
                    ErrorCode.RESPONSE_TYPE_NOT_CODE.getCode(),
                    null
            );
        }

        return true;
    }

    private boolean matchesPersistedClient(AuthRequest authRequest) throws ClientNotFoundException, UnAuthorizedResponseTypeException, RedirectUriMismatchException {

        Client client;
        try {
            client = clientRepository.getByUUID(authRequest.getClientId());
        } catch (RecordNotFoundException e) {
            throw new ClientNotFoundException("The Client was not found", e, ErrorCode.CLIENT_NOT_FOUND.getCode());
        }

        if ( client.getResponseType() != authRequest.getResponseType() ) {
            throw new UnAuthorizedResponseTypeException(
                    "Response Type requested doesnt match client's response type",
                    ErrorCode.RESPONSE_TYPE_MISMATCH.getCode(),
                    client.getRedirectURI()
            );
        }

        if (authRequest.getRedirectURI().isPresent() && !client.getRedirectURI().equals(authRequest.getRedirectURI().get()) ) {
            throw new RedirectUriMismatchException(
                    "Redirect URI requested doesnt match client's redirect uri",
                    ErrorCode.REDIRECT_URI_MISMATCH.getCode()
            );
        }

        return true;
    }
}
