package net.tokensmith.authorization.persistence.mapper;

import org.apache.ibatis.annotations.Param;
import net.tokensmith.authorization.persistence.entity.Configuration;

import java.util.UUID;

/**
 * Created by tommackenzie on 11/5/16.
 */
public interface ConfigurationMapper {
    Configuration get();
    void updateAccessTokenSize(@Param("id") UUID id, @Param("size") Integer size);
    void updateAuthorizationCodeSize(@Param("id") UUID id, @Param("size") Integer size);
    void updateRefreshTokenSize(@Param("id") UUID id, @Param("size") Integer size);
}
