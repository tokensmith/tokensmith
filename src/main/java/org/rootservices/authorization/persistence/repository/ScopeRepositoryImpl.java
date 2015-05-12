package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.Scope;
import org.rootservices.authorization.persistence.mapper.ScopeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by tommackenzie on 5/12/15.
 */
@Component
public class ScopeRepositoryImpl implements ScopeRepository {

    private ScopeMapper scopeMapper;

    @Autowired
    public ScopeRepositoryImpl(ScopeMapper scopeMapper) {
        this.scopeMapper = scopeMapper;
    }

    public ScopeRepositoryImpl(){}

    @Override
    public void insert(Scope scope) {
        scopeMapper.insert(scope);
    }
}
