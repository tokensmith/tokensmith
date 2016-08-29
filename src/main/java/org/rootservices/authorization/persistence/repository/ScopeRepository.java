package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.Scope;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;

import java.util.List;

/**
 * Created by tommackenzie on 5/12/15.
 */
public interface ScopeRepository {
    void insert(Scope scope);
    List<Scope> findByNames(List<String> names);
    Scope findByName(String name) throws RecordNotFoundException;
}
