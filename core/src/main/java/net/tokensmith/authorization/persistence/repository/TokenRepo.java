package net.tokensmith.authorization.persistence.repository;


import net.tokensmith.authorization.persistence.factory.DuplicateRecordExceptionFactory;
import net.tokensmith.authorization.persistence.mapper.TokenMapper;
import net.tokensmith.repository.entity.Token;
import net.tokensmith.repository.exceptions.DuplicateRecordException;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.repository.repo.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;


@Component
public class TokenRepo implements TokenRepository {
    private static String SCHEMA = "token";
    private static String RECORD_NOT_FOUND_MSG = "Could not find token record.";

    private DuplicateRecordExceptionFactory duplicateRecordExceptionFactory;
    private TokenMapper tokenMapper;

    @Autowired
    public TokenRepo(TokenMapper tokenMapper, DuplicateRecordExceptionFactory duplicateRecordExceptionFactory) {
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

    @Override
    public void revokeActive(UUID resourceOwnerId) {
        tokenMapper.revokeActive(resourceOwnerId);
    }
}
