package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.authorization.persistence.entity.Client;
import net.tokensmith.authorization.persistence.exceptions.RecordNotFoundException;

import java.util.UUID;

/**
 * Created by tommackenzie on 11/16/14.
 */
public interface ClientRepository {
    Client getById(UUID id) throws RecordNotFoundException;
    void insert(Client client);
}
