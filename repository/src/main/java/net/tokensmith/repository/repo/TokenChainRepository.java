package net.tokensmith.repository.repo;

import net.tokensmith.repository.entity.TokenChain;
import net.tokensmith.repository.exceptions.DuplicateRecordException;

/**
 * Created by tommackenzie on 10/8/16.
 */
public interface TokenChainRepository {
    void insert(TokenChain tokenChain) throws DuplicateRecordException;
}
