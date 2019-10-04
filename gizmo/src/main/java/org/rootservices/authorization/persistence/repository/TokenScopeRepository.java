package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.TokenScope;

/**
 * Created by tommackenzie on 4/18/16.
 */
public interface TokenScopeRepository {
    void insert(TokenScope tokenScope);
}
