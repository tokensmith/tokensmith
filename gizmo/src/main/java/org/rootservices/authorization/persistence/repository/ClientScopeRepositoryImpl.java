package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.ClientScope;
import org.rootservices.authorization.persistence.mapper.ClientScopesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by tommackenzie on 5/12/15.
 */
@Repository
public class ClientScopeRepositoryImpl implements ClientScopesRepository {

    private ClientScopesMapper clientScopesMapper;

    public ClientScopeRepositoryImpl() {}

    @Autowired
    public ClientScopeRepositoryImpl(ClientScopesMapper clientScopesMapper) {
        this.clientScopesMapper = clientScopesMapper;
    }

    @Override
    public void insert(ClientScope clientScope) {
        clientScopesMapper.insert(clientScope);
    }
}
