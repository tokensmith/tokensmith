package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.authorization.persistence.entity.TokenScope;

/**
 * Created by tommackenzie on 4/18/16.
 */
public interface TokenScopeRepository {
    void insert(TokenScope tokenScope);
}
