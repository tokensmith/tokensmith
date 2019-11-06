package net.tokensmith.repository.repo;

import net.tokensmith.repository.entity.AuthCode;
import net.tokensmith.repository.exceptions.DuplicateRecordException;
import net.tokensmith.repository.exceptions.RecordNotFoundException;

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
