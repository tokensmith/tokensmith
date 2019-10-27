package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.authorization.persistence.entity.ClientScope;
import net.tokensmith.authorization.persistence.mapper.ClientScopesMapper;
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
