package net.tokensmith.repository.repo;

import net.tokensmith.repository.entity.ConfidentialClient;
import net.tokensmith.repository.exceptions.RecordNotFoundException;

import java.util.UUID;

/**
 * Created by tommackenzie on 5/24/15.
 */
public interface ConfidentialClientRepository {
    void insert(ConfidentialClient confidentialClient);
    ConfidentialClient getByClientId(UUID clientID) throws RecordNotFoundException;
}
