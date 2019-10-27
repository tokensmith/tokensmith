package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.authorization.persistence.entity.Client;
import net.tokensmith.authorization.persistence.exceptions.RecordNotFoundException;
import net.tokensmith.authorization.persistence.mapper.ClientMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by tommackenzie on 11/16/14.
 */
@Component
public class ClientRepositoryImpl implements ClientRepository {

    @Autowired
    private ClientMapper clientMapper;

    public ClientRepositoryImpl() {}

    public ClientRepositoryImpl(ClientMapper clientMapper) {
        this.clientMapper = clientMapper;
    }

    @Override
    public Client getById(UUID uuid) throws RecordNotFoundException {
        Client client = clientMapper.getById(uuid);
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
