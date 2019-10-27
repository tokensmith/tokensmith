package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.authorization.persistence.entity.TokenChain;
import net.tokensmith.authorization.persistence.exceptions.DuplicateRecordException;
import net.tokensmith.authorization.persistence.mapper.TokenChainMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

/**
 * Created by tommackenzie on 10/8/16.
 */
@Component
public class TokenChainRepositoryImpl implements TokenChainRepository {
    private TokenChainMapper tokenChainMapper;

    private static String DUPLICATE_ERROR_MSG = "Could not insert token chain - refresh token was already used";

    @Autowired
    public TokenChainRepositoryImpl(TokenChainMapper tokenChainMapper) {
        this.tokenChainMapper = tokenChainMapper;
    }

    @Override
    public void insert(TokenChain tokenChain) throws DuplicateRecordException {
        try {
            tokenChainMapper.insert(tokenChain);
        } catch (DuplicateKeyException e) {
            throw new DuplicateRecordException(DUPLICATE_ERROR_MSG, e);
        }
    }
}
