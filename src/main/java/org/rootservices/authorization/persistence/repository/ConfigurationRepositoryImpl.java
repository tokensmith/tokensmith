package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.Configuration;
import org.rootservices.authorization.persistence.mapper.ConfigurationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by tommackenzie on 11/5/16.
 */
@Component
public class ConfigurationRepositoryImpl implements ConfigurationRepository {
    private ConfigurationMapper tokenSizeMapper;

    @Autowired
    public ConfigurationRepositoryImpl(ConfigurationMapper tokenSizeMapper) {
        this.tokenSizeMapper = tokenSizeMapper;
    }

    @Override
    public Configuration get() {
        return tokenSizeMapper.get();
    }

    @Override
    public void updateAccessTokenSize(UUID id, Integer size) {
        tokenSizeMapper.updateAccessTokenSize(id, size);
    }

    @Override
    public void updateAuthorizationCodeSize(UUID id, Integer size) {
        tokenSizeMapper.updateAuthorizationCodeSize(id, size);
    }

    @Override
    public void updateRefreshTokenSize(UUID id, Integer size) {
        tokenSizeMapper.updateRefreshTokenSize(id, size);
    }
}
