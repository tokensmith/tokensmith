package net.tokensmith.repository.repo;

import net.tokensmith.repository.entity.Scope;
import net.tokensmith.repository.exceptions.RecordNotFoundException;

import java.util.List;

/**
 * Created by tommackenzie on 5/12/15.
 */
public interface ScopeRepository {
    void insert(Scope scope);
    List<Scope> findByNames(List<String> names);
    Scope findByName(String name) throws RecordNotFoundException;
}
