package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.Scope;
import org.rootservices.authorization.persistence.mapper.ScopeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public List<Scope> findByName(List<String> names) {
        List<Scope> scopes = new ArrayList<>();
        if (names != null && names.size() > 0) {
            scopes = scopeMapper.findByName(names);
        }
        return scopes;
    }
}
