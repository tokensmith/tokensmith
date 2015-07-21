package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.Token;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.mapper.TokenMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by tommackenzie on 5/23/15.
 */
@Component
public class TokenRepositoryImpl implements TokenRepository {
    private TokenMapper tokenMapper;

    @Autowired
    public TokenRepositoryImpl(TokenMapper tokenMapper) {
        this.tokenMapper = tokenMapper;
    }

    @Override
    public void insert(Token token) throws DuplicateRecordException {
        try {
            tokenMapper.insert(token);
        } catch (DuplicateKeyException e) {
            throw new DuplicateRecordException("Could not insert token record.", e);
        }
    }

    @Override
    public void revoke(UUID authCodeUUID) {
        tokenMapper.revoke(authCodeUUID);
    }
}
