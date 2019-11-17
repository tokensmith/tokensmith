package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.repository.entity.AuthCode;
import net.tokensmith.authorization.persistence.factory.DuplicateRecordExceptionFactory;
import net.tokensmith.authorization.persistence.mapper.AuthCodeMapper;
import net.tokensmith.repository.exceptions.DuplicateRecordException;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.repository.repo.AuthCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by tommackenzie on 4/10/15.
 */
@Component
public class AuthCodeRepo implements AuthCodeRepository {
    private static String SCHEMA = "auth_code";
    private static String RECORD_NOT_FOUND_MSG = "AuthCode record was not found.";

    private DuplicateRecordExceptionFactory duplicateRecordExceptionFactory;
    private AuthCodeMapper authCodeMapper;

    @Autowired
    public AuthCodeRepo(DuplicateRecordExceptionFactory duplicateRecordExceptionFactory, AuthCodeMapper authCodeMapper) {
        this.duplicateRecordExceptionFactory = duplicateRecordExceptionFactory;
        this.authCodeMapper = authCodeMapper;
    }

    @Override
    public void insert(AuthCode authCode) throws DuplicateRecordException {
        try {
            authCodeMapper.insert(authCode);
        } catch (DuplicateKeyException e) {
            throw duplicateRecordExceptionFactory.make(e, SCHEMA);
        }
    }

    @Override
    public AuthCode getByClientIdAndAuthCode(UUID clientUUID, String code) throws RecordNotFoundException {
        AuthCode authCode = authCodeMapper.getByClientIdAndAuthCode(clientUUID, code);

        if (authCode == null) {
            throw new RecordNotFoundException(RECORD_NOT_FOUND_MSG);
        }

        return authCode;
    }

    @Override
    public AuthCode getById(UUID id) throws RecordNotFoundException {
        AuthCode authCode = authCodeMapper.getById(id);

        if (authCode == null) {
            throw new RecordNotFoundException(RECORD_NOT_FOUND_MSG);
        }

        return authCode;
    }

    @Override
    public void revokeById(UUID id) {
        authCodeMapper.revokeById(id);
    }

}
