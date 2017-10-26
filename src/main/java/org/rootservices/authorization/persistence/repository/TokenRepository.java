package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.Token;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;

import java.time.OffsetDateTime;
import java.util.UUID;


public interface TokenRepository {
    void insert(Token token) throws DuplicateRecordException;
    Token getByAuthCodeId(UUID authCodeId) throws RecordNotFoundException;
    void revokeByAuthCodeId(UUID authCodeId);
    void revokeById(UUID id);
    void updateExpiresAtByAccessToken(OffsetDateTime expiresAt, String accessToken);
    void revokeAll(UUID resourceOwnerId);
}
