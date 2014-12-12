package org.rootservices.authorization.codegrant.params;

import org.rootservices.authorization.codegrant.exception.client.InformClientException;
import org.rootservices.authorization.codegrant.exception.client.ManyResponseTypesException;
import org.rootservices.authorization.codegrant.exception.client.MissingResponseTypeException;
import org.rootservices.authorization.codegrant.exception.resourceowner.InformResourceOwnerException;
import org.rootservices.authorization.codegrant.exception.resourceowner.InvalidClientIdException;
import org.rootservices.authorization.codegrant.exception.resourceowner.ManyClientIdsException;
import org.rootservices.authorization.codegrant.exception.resourceowner.MissingClientIdException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Created by tommackenzie on 11/27/14.
 */
@Component
public class ValidateParamsImpl implements ValidateParams {

    @Override
    public boolean run(List<String> clientIds, List<String> responseTypes) throws InformResourceOwnerException, InformClientException {

        if ( hasOneValidEntry(clientIds) ) {
            throw new MissingClientIdException("Missing Client Id");
        }

        if ( clientIds.size() > 1) {
            throw new ManyClientIdsException("Request had more than 1 Client Ids");
        }

        try {
            UUID.fromString(clientIds.get(0));
        } catch ( IllegalArgumentException e) {
            throw new InvalidClientIdException("Client Id is not UUID");
        }

        if ( hasOneValidEntry(responseTypes) ) {
            throw new MissingResponseTypeException("Missing Response Type");
        }

        if ( responseTypes.size() > 1) {
            throw new ManyResponseTypesException("Request had more than 1 Response Types");
        }
        return true;
    }

    private boolean hasOneValidEntry(List<String> items) {
        return ( items == null || items.size() == 0 || items.get(0).isEmpty());
    }
}
