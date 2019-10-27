package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.authorization.persistence.entity.Token;
import net.tokensmith.authorization.persistence.exceptions.DuplicateRecordException;
import net.tokensmith.authorization.persistence.exceptions.RecordNotFoundException;

import java.time.OffsetDateTime;
import java.util.UUID;


public interface TokenRepository {
    void insert(Token token) throws DuplicateRecordException;
    Token getByAuthCodeId(UUID authCodeId) throws RecordNotFoundException;
    void revokeByAuthCodeId(UUID authCodeId);
    void revokeById(UUID id);
    void updateExpiresAtByAccessToken(OffsetDateTime expiresAt, String accessToken);
    void revokeActive(UUID resourceOwnerId);
}
