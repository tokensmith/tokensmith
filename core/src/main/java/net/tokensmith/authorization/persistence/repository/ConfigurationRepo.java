package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.authorization.persistence.mapper.ConfigurationMapper;
import net.tokensmith.repository.entity.Configuration;
import net.tokensmith.repository.repo.ConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by tommackenzie on 11/5/16.
 */
@Component
public class ConfigurationRepo implements ConfigurationRepository {
    private ConfigurationMapper configurationMapper;

    @Autowired
    public ConfigurationRepo(ConfigurationMapper configurationMapper) {
        this.configurationMapper = configurationMapper;
    }

    @Override
    public Configuration get() {
        return configurationMapper.get();
    }

    @Override
    public void updateAccessTokenSize(UUID id, Integer size) {
        configurationMapper.updateAccessTokenSize(id, size);
    }

    @Override
    public void updateAuthorizationCodeSize(UUID id, Integer size) {
        configurationMapper.updateAuthorizationCodeSize(id, size);
    }

    @Override
    public void updateRefreshTokenSize(UUID id, Integer size) {
        configurationMapper.updateRefreshTokenSize(id, size);
    }
}
