package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.authorization.persistence.mapper.ClientScopesMapper;
import net.tokensmith.repository.entity.ClientScope;
import net.tokensmith.repository.repo.ClientScopesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by tommackenzie on 5/12/15.
 */
@Repository
public class ClientScopeRepo implements ClientScopesRepository {

    private ClientScopesMapper clientScopesMapper;

    public ClientScopeRepo() {}

    @Autowired
    public ClientScopeRepo(ClientScopesMapper clientScopesMapper) {
        this.clientScopesMapper = clientScopesMapper;
    }

    @Override
    public void insert(ClientScope clientScope) {
        clientScopesMapper.insert(clientScope);
    }
}
