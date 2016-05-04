package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.AuthCode;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.mapper.AuthCodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by tommackenzie on 4/10/15.
 */
@Component
public class AuthCodeRepositoryImpl implements AuthCodeRepository {
    private static String DUPLICATE_RECORD_MSG = "Could not insert auth_code record.";
    private static String RECORD_NOT_FOUND_MSG = "AuthCode record was not found.";

    private AuthCodeMapper authCodeMapper;

    @Autowired
    public AuthCodeRepositoryImpl(AuthCodeMapper authCodeMapper) {
        this.authCodeMapper = authCodeMapper;
    }

    @Override
    public void insert(AuthCode authCode) throws DuplicateRecordException {
        try {
            authCodeMapper.insert(authCode);
        } catch (DuplicateKeyException e) {
            throw new DuplicateRecordException(DUPLICATE_RECORD_MSG, e);
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
