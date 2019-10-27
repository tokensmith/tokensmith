package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.authorization.persistence.entity.TokenChain;
import net.tokensmith.authorization.persistence.exceptions.DuplicateRecordException;

/**
 * Created by tommackenzie on 10/8/16.
 */
public interface TokenChainRepository {
    void insert(TokenChain tokenChain) throws DuplicateRecordException;
}
