package org.rootservices.persistence.repository;

import org.rootservices.persistence.entity.Client;
import org.rootservices.persistence.exceptions.RecordNotFoundException;
import org.rootservices.persistence.mapper.ClientMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

/**
 * Created by tommackenzie on 11/16/14.
 */
public class ClientRepositoryImpl implements ClientRepository {

    @Autowired
    private ClientMapper clientMapper;

    @Override
    public Client getByUUID(UUID uuid) throws RecordNotFoundException {
        Client client = clientMapper.getByUUID(uuid);
        if (client != null) {
            return client;
        }

        throw new RecordNotFoundException("Client: " + uuid.toString());
    }

    @Override
    public void insert(Client client) {
        clientMapper.insert(client);
    }
}
