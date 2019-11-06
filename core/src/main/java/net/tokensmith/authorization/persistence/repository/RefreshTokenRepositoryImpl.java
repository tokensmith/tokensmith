package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.repository.entity.RefreshToken;
import net.tokensmith.authorization.persistence.factory.DuplicateRecordExceptionFactory;
import net.tokensmith.authorization.persistence.mapper.RefreshTokenMapper;
import net.tokensmith.repository.exceptions.DuplicateRecordException;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.repository.repo.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.UUID;


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

    @Override
    public void revokeActive(UUID resourceOwnerId) {
        refreshTokenMapper.revokeActive(resourceOwnerId);
    }
}
