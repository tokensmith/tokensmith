package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.RefreshToken;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.factory.DuplicateRecordExceptionFactory;
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
    private static String SCHEMA = "refresh_token";

    private RefreshTokenMapper refreshTokenMapper;
    private DuplicateRecordExceptionFactory duplicateRecordExceptionFactory;

    @Autowired
    public RefreshTokenRepositoryImpl(RefreshTokenMapper refreshTokenMapper, DuplicateRecordExceptionFactory duplicateRecordExceptionFactory) {
        this.refreshTokenMapper = refreshTokenMapper;
        this.duplicateRecordExceptionFactory = duplicateRecordExceptionFactory;
    }

    @Override
    public void insert(RefreshToken refreshToken) throws DuplicateRecordException {
        try {
            refreshTokenMapper.insert(refreshToken);
        } catch (DuplicateKeyException e) {
            throw duplicateRecordExceptionFactory.make(e, SCHEMA);
        }
    }

    @Override
    public RefreshToken getByClientIdAndAccessToken(UUID clientId, String token) throws RecordNotFoundException {
        RefreshToken refreshToken = refreshTokenMapper.getByClientIdAndAccessToken(clientId, token);
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
