package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.Scope;

/**
 * Created by tommackenzie on 5/12/15.
 */
public interface ScopeRepository {
    void insert(Scope scope);
}
