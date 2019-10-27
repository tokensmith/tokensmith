package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.authorization.persistence.entity.AuthCode;
import net.tokensmith.authorization.persistence.exceptions.DuplicateRecordException;
import net.tokensmith.authorization.persistence.exceptions.RecordNotFoundException;

import java.util.UUID;

/**
 * Created by tommackenzie on 4/10/15.
 */
public interface AuthCodeRepository {
    void insert(AuthCode authCode) throws DuplicateRecordException;
    AuthCode getByClientIdAndAuthCode(UUID clientUUID, String code) throws RecordNotFoundException;
    AuthCode getById(UUID id) throws RecordNotFoundException;
    void revokeById(UUID id);
}
