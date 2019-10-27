package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.authorization.persistence.entity.AccessRequestScope;

/**
 * Created by tommackenzie on 5/19/15.
 */
public interface AccessRequestScopesRepository {
    void insert(AccessRequestScope accessRequestScope);
}
