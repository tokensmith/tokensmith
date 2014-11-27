package org.rootservices.authorization.codegrant.builder;

import org.rootservices.authorization.codegrant.exception.resourceowner.InformResourceOwnerException;
import org.rootservices.authorization.codegrant.exception.resourceowner.InvalidClientIdException;
import org.rootservices.authorization.codegrant.request.AuthRequest;
import org.rootservices.authorization.persistence.entity.ResponseType;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by tommackenzie on 11/27/14.
 */
@Component
public class AuthRequestBuilderImpl implements AuthRequestBuilder{

    public AuthRequestBuilderImpl() {};

    public AuthRequest build(String clientIdentifier, String responseType) throws InformResourceOwnerException {

        UUID clientId;
        ResponseType clientResponseType;

        try {
            clientId = UUID.fromString(clientIdentifier);
        } catch (IllegalArgumentException e ) {
            throw new InvalidClientIdException("Client Id is not a UUID", e);
        } catch( NullPointerException e) {
            clientId = null;
        }

        try {
            clientResponseType = ResponseType.valueOf(responseType.toUpperCase());
        } catch(IllegalArgumentException | NullPointerException e) {
            clientResponseType = null;
        }

        AuthRequest authRequest = new AuthRequest();
        authRequest.setClientId(clientId);
        authRequest.setResponseType(clientResponseType);

        return authRequest;
    }
}
