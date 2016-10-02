package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.ClientToken;

/**
 * Created by tommackenzie on 10/2/16.
 */
public interface ClientTokenRepository {
    void insert(ClientToken clientToken);
}
