package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.Token;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
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
    private static String DUPLICATE_RECORD_MSG = "Could not insert token record.";
    private static String RECORD_NOT_FOUND_MSG = "Could not find token record.";
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
            throw new DuplicateRecordException(DUPLICATE_RECORD_MSG, e);
        }
    }

    @Override
    public void revokeByAuthCodeId(UUID authCodeId) {
        tokenMapper.revokeByAuthCodeId(authCodeId);
    }

    @Override
    public Token getByAuthCodeId(UUID authCodeId) throws RecordNotFoundException {
        Token token = tokenMapper.getByAuthCodeId(authCodeId);

        if (token == null) {
            throw new RecordNotFoundException(RECORD_NOT_FOUND_MSG);
        }

        return token;
    }
}
