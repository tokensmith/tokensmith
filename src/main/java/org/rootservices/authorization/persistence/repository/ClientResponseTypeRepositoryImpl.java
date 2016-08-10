package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.ClientResponseType;
import org.rootservices.authorization.persistence.mapper.ClientResponseTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by tommackenzie on 8/9/16.
 */
@Component
public class ClientResponseTypeRepositoryImpl implements ClientResponseTypeRepository {
    private ClientResponseTypeMapper clientResponseTypeMapper;

    @Autowired
    public ClientResponseTypeRepositoryImpl(ClientResponseTypeMapper clientResponseTypeMapper) {
        this.clientResponseTypeMapper = clientResponseTypeMapper;
    }

    @Override
    public void insert(ClientResponseType clientResponseType) {
        clientResponseTypeMapper.insert(clientResponseType);
    }
}
