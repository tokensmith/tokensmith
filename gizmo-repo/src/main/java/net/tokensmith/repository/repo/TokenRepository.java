package net.tokensmith.repository.repo;

import net.tokensmith.repository.entity.Token;
import net.tokensmith.repository.exceptions.DuplicateRecordException;
import net.tokensmith.repository.exceptions.RecordNotFoundException;

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
