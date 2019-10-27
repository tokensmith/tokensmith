package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.authorization.persistence.entity.Configuration;

import java.util.UUID;

/**
 * Created by tommackenzie on 11/5/16.
 */
public interface ConfigurationRepository {
    Configuration get();
    void updateAccessTokenSize(UUID id, Integer size);
    void updateAuthorizationCodeSize(UUID id, Integer size);
    void updateRefreshTokenSize(UUID id, Integer size);
}
