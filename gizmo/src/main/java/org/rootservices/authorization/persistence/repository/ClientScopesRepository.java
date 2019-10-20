package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.ClientScope;

/**
 * Created by tommackenzie on 5/12/15.
 */
public interface ClientScopesRepository {
    void insert(ClientScope clientScope);
}
