package org.rootservices.authorization.persistence.repository;


import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.factory.DuplicateRecordExceptionFactory;
import org.rootservices.authorization.persistence.mapper.ResourceOwnerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by tommackenzie on 10/11/14.
 */
@Component
public class ResourceOwnerRepositoryImpl implements ResourceOwnerRepository {
    private static String SCHEMA = "resource_owner";
    private DuplicateRecordExceptionFactory duplicateRecordExceptionFactory;

    private ResourceOwnerMapper resourceOwnerMapper;

    @Autowired
    public ResourceOwnerRepositoryImpl(ResourceOwnerMapper resourceOwnerMapper, DuplicateRecordExceptionFactory duplicateRecordExceptionFactory){
        this.resourceOwnerMapper = resourceOwnerMapper;
        this.duplicateRecordExceptionFactory = duplicateRecordExceptionFactory;
    }

    public ResourceOwner getById(UUID id) throws RecordNotFoundException {
        ResourceOwner resourceOwner = resourceOwnerMapper.getById(id);
        if (resourceOwner != null) {
            return resourceOwner;
        }

        throw new RecordNotFoundException("Resource Owner: " + id.toString());
    }

    public ResourceOwner getByEmail(String email) throws RecordNotFoundException {
        ResourceOwner resourceOwner = resourceOwnerMapper.getByEmail(email);
        if (resourceOwner != null) {
            return resourceOwner;
        }

        throw new RecordNotFoundException("Resource Owner: " + email);
    }

    @Override
    public ResourceOwner getByAccessToken(String accessToken) throws RecordNotFoundException {
        ResourceOwner resourceOwner = resourceOwnerMapper.getByAccessToken(accessToken);
        if (resourceOwner != null) {
            return resourceOwner;
        }

        throw new RecordNotFoundException("Resource Owner was not found");
    }

    @Override
    public ResourceOwner getByAccessTokenWithProfileAndTokens(String accessToken) throws RecordNotFoundException {
        ResourceOwner resourceOwner = resourceOwnerMapper.getByAccessTokenWithProfileAndTokens(accessToken);
        if (resourceOwner != null) {
            return resourceOwner;
        }

        throw new RecordNotFoundException("Resource Owner was not found");
    }

    public void insert(ResourceOwner resourceOwner) throws DuplicateRecordException {
        try {
            resourceOwnerMapper.insert(resourceOwner);
        } catch (DuplicateKeyException e) {
            throw duplicateRecordExceptionFactory.make(e, SCHEMA);
        }
    }

    @Override
    public void setEmailVerified(UUID id) {
        resourceOwnerMapper.setEmailVerified(id);
    }
}
