package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.authorization.persistence.mapper.ScopeMapper;
import net.tokensmith.repository.entity.Scope;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.repository.repo.ScopeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tommackenzie on 5/12/15.
 */
@Component
public class ScopeRepo implements ScopeRepository {

    private ScopeMapper scopeMapper;

    @Autowired
    public ScopeRepo(ScopeMapper scopeMapper) {
        this.scopeMapper = scopeMapper;
    }

    public ScopeRepo(){}

    @Override
    public void insert(Scope scope) {
        scopeMapper.insert(scope);
    }

    @Override
    public List<Scope> findByNames(List<String> names) {
        List<Scope> scopes = new ArrayList<>();
        if (names != null && names.size() > 0) {
            scopes = scopeMapper.findByNames(names);
        }
        return scopes;
    }

    @Override
    public Scope findByName(String name) throws RecordNotFoundException {
        Scope scope = scopeMapper.findByName(name);

        if (scope == null) {
            throw new RecordNotFoundException();
        }

        return scope;
    }


}
