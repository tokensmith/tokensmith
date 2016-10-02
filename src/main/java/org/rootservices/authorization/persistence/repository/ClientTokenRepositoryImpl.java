package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.ClientToken;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.mapper.ClientTokenMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by tommackenzie on 10/2/16.
 */
@Component
public class ClientTokenRepositoryImpl implements ClientTokenRepository {
    private ClientTokenMapper clientTokenMapper;

    @Autowired
    public ClientTokenRepositoryImpl(ClientTokenMapper clientTokenMapper) {
        this.clientTokenMapper = clientTokenMapper;
    }

    @Override
    public void insert(ClientToken clientToken) {
        clientTokenMapper.insert(clientToken);
    }

    @Override
    public ClientToken getByTokenId(UUID tokenId) throws RecordNotFoundException {
        ClientToken clientToken = clientTokenMapper.getByTokenId(tokenId);
        if (clientToken == null) {
            throw new RecordNotFoundException("Could not find client token");
        }
        return clientToken;
    }
}
