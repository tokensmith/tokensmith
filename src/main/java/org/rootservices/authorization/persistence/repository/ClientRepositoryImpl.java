package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.Client;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.mapper.ClientMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Created by tommackenzie on 11/16/14.
 */
@Component
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
