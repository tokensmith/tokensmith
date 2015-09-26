package org.rootservices.authorization.grant.code.protocol.authorization.builder.required;

import org.rootservices.authorization.grant.code.protocol.authorization.builder.exception.ClientIdException;

import java.util.List;
import java.util.UUID;

/**
 * Created by tommackenzie on 1/31/15.
 */
public interface ClientIdBuilder {
    UUID makeClientId(List<String> clientIds) throws ClientIdException;
}
