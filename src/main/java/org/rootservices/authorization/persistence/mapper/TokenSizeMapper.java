package org.rootservices.authorization.persistence.mapper;

import org.apache.ibatis.annotations.Param;
import org.rootservices.authorization.persistence.entity.TokenSize;

import java.util.UUID;

/**
 * Created by tommackenzie on 11/5/16.
 */
public interface TokenSizeMapper {
    TokenSize get();
    void updateAccessTokenSize(@Param("id") UUID id, @Param("size") Integer size);
    void updateAuthorizationCodeSize(@Param("id") UUID id, @Param("size") Integer size);
    void updateRefreshTokenSize(@Param("id") UUID id, @Param("size") Integer size);
}
