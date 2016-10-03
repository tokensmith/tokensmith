package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.RefreshToken;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.mapper.RefreshTokenMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

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
    public RefreshToken getByToken(String token) {
        RefreshToken refreshToken = refreshTokenMapper.getByToken(token);
        return refreshToken;
    }
}
