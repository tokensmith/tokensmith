package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.repository.entity.AccessRequestScope;
import net.tokensmith.authorization.persistence.mapper.AccessRequestScopesMapper;
import net.tokensmith.repository.repo.AccessRequestScopesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by tommackenzie on 5/19/15.
 *
 */
@Component
public class AccessRequestScopesRepositoryImpl implements AccessRequestScopesRepository {

    private AccessRequestScopesMapper accessRequestScopesMapper;

    @Autowired
    public AccessRequestScopesRepositoryImpl(AccessRequestScopesMapper accessRequestScopesMapper) {
        this.accessRequestScopesMapper = accessRequestScopesMapper;
    }

    @Override
    public void insert(AccessRequestScope accessRequestScope) {
        accessRequestScopesMapper.insert(accessRequestScope);
    }
}
