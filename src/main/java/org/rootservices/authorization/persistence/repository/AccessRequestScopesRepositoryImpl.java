package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.AccessRequestScope;
import org.rootservices.authorization.persistence.mapper.AccessRequestScopesMapper;
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
