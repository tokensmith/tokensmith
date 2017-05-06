package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.Token;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.factory.DuplicateRecordExceptionFactory;
import org.rootservices.authorization.persistence.mapper.TokenMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;


/**
 * Created by tommackenzie on 5/23/15.
 */
@Component
public class TokenRepositoryImpl implements TokenRepository {
    private static String SCHEMA = "token";
    private static String RECORD_NOT_FOUND_MSG = "Could not find token record.";

    private DuplicateRecordExceptionFactory duplicateRecordExceptionFactory;
    private TokenMapper tokenMapper;

    @Autowired
    public TokenRepositoryImpl(TokenMapper tokenMapper, DuplicateRecordExceptionFactory duplicateRecordExceptionFactory) {
        this.tokenMapper = tokenMapper;
        this.duplicateRecordExceptionFactory = duplicateRecordExceptionFactory;
    }

    @Override
    public void insert(Token token) throws DuplicateRecordException {
        try {
            tokenMapper.insert(token);
        } catch (DuplicateKeyException e) {
            throw duplicateRecordExceptionFactory.make(e, SCHEMA);
        }
    }

    @Override
    public void revokeByAuthCodeId(UUID authCodeId) {
        tokenMapper.revokeByAuthCodeId(authCodeId);
    }

    @Override
    public void revokeById(UUID id) {
        tokenMapper.revokeById(id);
    }

    @Override
    public void updateExpiresAtByAccessToken(OffsetDateTime expiresAt, String accessToken) {
        tokenMapper.updateExpiresAtByAccessToken(expiresAt, accessToken);
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
