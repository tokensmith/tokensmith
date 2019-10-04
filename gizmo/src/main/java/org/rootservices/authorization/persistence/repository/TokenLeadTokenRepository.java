package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.TokenLeadToken;

/**
 * Created by tommackenzie on 12/1/16.
 */
public interface TokenLeadTokenRepository {
    void insert(TokenLeadToken tokenLeadToken);
}
