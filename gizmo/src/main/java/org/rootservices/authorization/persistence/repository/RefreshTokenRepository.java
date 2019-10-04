package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.RefreshToken;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;

import java.util.UUID;


public interface RefreshTokenRepository {
    void insert(RefreshToken refreshToken) throws DuplicateRecordException;
    RefreshToken getByClientIdAndAccessToken(UUID clientId, String accessToken) throws RecordNotFoundException;
    RefreshToken getByTokenId(UUID tokenId) throws RecordNotFoundException;
    void revokeByAuthCodeId(UUID authCodeId);
    void revokeByTokenId(UUID tokenId);
    void revokeActive(UUID resourceOwnerId);
}
