package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.Token;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;

import java.util.UUID;

/**
 * Created by tommackenzie on 5/23/15.
 */
public interface TokenRepository {
    void insert(Token token) throws DuplicateRecordException;
    void revokeByAuthCodeId(UUID authCodeId);
    Token getByAuthCodeId(UUID authCodeId) throws RecordNotFoundException;
}
