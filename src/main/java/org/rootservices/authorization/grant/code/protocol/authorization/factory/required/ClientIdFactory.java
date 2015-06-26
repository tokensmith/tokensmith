package org.rootservices.authorization.grant.code.protocol.authorization.factory.required;

import org.rootservices.authorization.grant.code.protocol.authorization.factory.exception.ClientIdException;

import java.util.List;
import java.util.UUID;

/**
 * Created by tommackenzie on 1/31/15.
 */
public interface ClientIdFactory {
    public UUID makeClientId(List<String> clientIds) throws ClientIdException;
}
