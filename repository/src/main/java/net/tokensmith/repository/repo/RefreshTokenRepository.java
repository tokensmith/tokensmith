package net.tokensmith.repository.repo;

import net.tokensmith.repository.entity.RefreshToken;
import net.tokensmith.repository.exceptions.DuplicateRecordException;
import net.tokensmith.repository.exceptions.RecordNotFoundException;

import java.util.UUID;


public interface RefreshTokenRepository {
    void insert(RefreshToken refreshToken) throws DuplicateRecordException;
    RefreshToken getByClientIdAndAccessToken(UUID clientId, String accessToken) throws RecordNotFoundException;
    RefreshToken getByTokenId(UUID tokenId) throws RecordNotFoundException;
    void revokeByAuthCodeId(UUID authCodeId);
    void revokeByTokenId(UUID tokenId);
    void revokeActive(UUID resourceOwnerId);
}
