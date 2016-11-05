package org.rootservices.authorization.persistence.repository;

import org.apache.ibatis.annotations.Param;
import org.rootservices.authorization.persistence.entity.TokenSize;

import java.util.UUID;

/**
 * Created by tommackenzie on 11/5/16.
 */
public interface TokenSizeRepository {
    TokenSize get();
    void updateAccessTokenSize(UUID id, Integer size);
    void updateAuthorizationCodeSize(UUID id, Integer size);
    void updateRefreshTokenSize(UUID id, Integer size);
}
