package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.authorization.persistence.entity.ConfidentialClient;
import net.tokensmith.authorization.persistence.exceptions.RecordNotFoundException;

import java.util.UUID;

/**
 * Created by tommackenzie on 5/24/15.
 */
public interface ConfidentialClientRepository {
    void insert(ConfidentialClient confidentialClient);
    ConfidentialClient getByClientId(UUID clientID) throws RecordNotFoundException;
}
