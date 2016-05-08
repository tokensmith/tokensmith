package org.rootservices.authorization.oauth2.grant.code.authorization.request.buider.required;

import org.rootservices.authorization.oauth2.grant.code.authorization.request.buider.exception.ClientIdException;

import java.util.List;
import java.util.UUID;

/**
 * Created by tommackenzie on 1/31/15.
 */
public interface ClientIdBuilder {
    UUID makeClientId(List<String> clientIds) throws ClientIdException;
}
