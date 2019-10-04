package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.AccessRequestScope;

/**
 * Created by tommackenzie on 5/19/15.
 */
public interface AccessRequestScopesRepository {
    void insert(AccessRequestScope accessRequestScope);
}
