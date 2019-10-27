package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.authorization.persistence.entity.ClientScope;

/**
 * Created by tommackenzie on 5/12/15.
 */
public interface ClientScopesRepository {
    void insert(ClientScope clientScope);
}
