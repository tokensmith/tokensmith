package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.TokenChain;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;

/**
 * Created by tommackenzie on 10/8/16.
 */
public interface TokenChainRepository {
    void insert(TokenChain tokenChain) throws DuplicateRecordException;
}
