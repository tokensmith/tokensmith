package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.RefreshToken;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;

/**
 * Created by tommackenzie on 10/3/16.
 */
public interface RefreshTokenRepository {
    void insert(RefreshToken refreshToken) throws DuplicateRecordException;
    RefreshToken getByToken(String token);
}
