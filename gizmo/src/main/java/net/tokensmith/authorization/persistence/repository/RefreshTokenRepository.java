package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.authorization.persistence.entity.RefreshToken;
import net.tokensmith.authorization.persistence.exceptions.DuplicateRecordException;
import net.tokensmith.authorization.persistence.exceptions.RecordNotFoundException;

import java.util.UUID;


public interface RefreshTokenRepository {
    void insert(RefreshToken refreshToken) throws DuplicateRecordException;
    RefreshToken getByClientIdAndAccessToken(UUID clientId, String accessToken) throws RecordNotFoundException;
    RefreshToken getByTokenId(UUID tokenId) throws RecordNotFoundException;
    void revokeByAuthCodeId(UUID authCodeId);
    void revokeByTokenId(UUID tokenId);
    void revokeActive(UUID resourceOwnerId);
}
