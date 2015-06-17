package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.AuthCode;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;

import java.util.UUID;

/**
 * Created by tommackenzie on 4/10/15.
 */
public interface AuthCodeRepository {
    void insert(AuthCode authCode);
    AuthCode getByClientUUIDAndAuthCode(UUID clientUUID, String code) throws RecordNotFoundException;
}
