package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.RefreshToken;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.mapper.RefreshTokenMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by tommackenzie on 10/3/16.
 */
@Component
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {
    private RefreshTokenMapper refreshTokenMapper;

    @Autowired
    public RefreshTokenRepositoryImpl(RefreshTokenMapper refreshTokenMapper) {
        this.refreshTokenMapper = refreshTokenMapper;
    }

    @Override
    public void insert(RefreshToken refreshToken) throws DuplicateRecordException {
        try {
            refreshTokenMapper.insert(refreshToken);
        } catch (DuplicateKeyException e) {
            throw new DuplicateRecordException("Could not insert refresh token", e);
        }
    }

    @Override
    public RefreshToken getByToken(String token) throws RecordNotFoundException {
        RefreshToken refreshToken = refreshTokenMapper.getByToken(token);
        if (refreshToken == null) {
            throw new RecordNotFoundException("refresh token not found.");
        }
        return refreshToken;
    }

    @Override
    public RefreshToken getByTokenId(UUID tokenId) throws RecordNotFoundException {
        RefreshToken refreshToken = refreshTokenMapper.getByTokenId(tokenId);
        if (refreshToken == null) {
            throw new RecordNotFoundException("refresh token not found.");
        }
        return refreshToken;
    }

    @Override
    public void revokeByAuthCodeId(UUID authCodeId) {
        refreshTokenMapper.revokeByAuthCodeId(authCodeId);
    }

    @Override
    public void revokeByTokenId(UUID tokenId) {
        refreshTokenMapper.revokeByTokenId(tokenId);
    }
}
