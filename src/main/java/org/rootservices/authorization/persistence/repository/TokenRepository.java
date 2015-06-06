package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.Token;

/**
 * Created by tommackenzie on 5/23/15.
 */
public interface TokenRepository {
    void insert(Token token);
}
