package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.ConfidentialClient;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;

import java.util.UUID;

/**
 * Created by tommackenzie on 5/24/15.
 */
public interface ConfidentialClientRepository {
    void insert(ConfidentialClient confidentialClient);
    ConfidentialClient getByClientUUID(UUID clientUUID) throws RecordNotFoundException;
}
