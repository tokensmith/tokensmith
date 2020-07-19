package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.authorization.persistence.mapper.ClientResponseTypeMapper;
import net.tokensmith.repository.entity.ClientResponseType;
import net.tokensmith.repository.repo.ClientResponseTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by tommackenzie on 8/9/16.
 */
@Component
public class ClientResponseTypeRepo implements ClientResponseTypeRepository {
    private ClientResponseTypeMapper clientResponseTypeMapper;

    @Autowired
    public ClientResponseTypeRepo(ClientResponseTypeMapper clientResponseTypeMapper) {
        this.clientResponseTypeMapper = clientResponseTypeMapper;
    }

    @Override
    public void insert(ClientResponseType clientResponseType) {
        clientResponseTypeMapper.insert(clientResponseType);
    }
}
