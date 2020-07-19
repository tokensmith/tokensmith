package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.authorization.persistence.mapper.ResourceOwnerTokenMapper;
import net.tokensmith.repository.entity.ResourceOwnerToken;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.repository.repo.ResourceOwnerTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by tommackenzie on 4/28/16.
 */
@Component
public class ResourceOwnerTokenRepo implements ResourceOwnerTokenRepository {
    private ResourceOwnerTokenMapper resourceOwnerTokenMapper;

    @Autowired
    public ResourceOwnerTokenRepo(ResourceOwnerTokenMapper resourceOwnerTokenMapper) {
        this.resourceOwnerTokenMapper = resourceOwnerTokenMapper;
    }

    @Override
    public ResourceOwnerToken getByAccessToken(String accessToken) throws RecordNotFoundException {
        ResourceOwnerToken resourceOwnerToken = resourceOwnerTokenMapper.getByAccessToken(accessToken);

        if (resourceOwnerToken == null) {
            throw new RecordNotFoundException("resource owneer token record not found");
        }
        return resourceOwnerToken;
    }

    @Override
    public void insert(ResourceOwnerToken resourceOwnerToken) {
        this.resourceOwnerTokenMapper.insert(resourceOwnerToken);
    }
}
