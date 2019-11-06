package net.tokensmith.repository.repo;

import net.tokensmith.repository.entity.Client;
import net.tokensmith.repository.exceptions.RecordNotFoundException;

import java.util.UUID;

/**
 * Created by tommackenzie on 11/16/14.
 */
public interface ClientRepository {
    Client getById(UUID id) throws RecordNotFoundException;
    void insert(Client client);
}
