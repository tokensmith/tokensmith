package net.tokensmith.authorization.persistence.repository;


import net.tokensmith.repository.entity.ResourceOwner;
import net.tokensmith.authorization.persistence.factory.DuplicateRecordExceptionFactory;
import net.tokensmith.authorization.persistence.mapper.ResourceOwnerMapper;
import net.tokensmith.repository.exceptions.DuplicateRecordException;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.repository.repo.ResourceOwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
public class ResourceOwnerRepo implements ResourceOwnerRepository {
    private static String SCHEMA = "resource_owner";
    private DuplicateRecordExceptionFactory duplicateRecordExceptionFactory;

    private ResourceOwnerMapper resourceOwnerMapper;

    @Autowired
    public ResourceOwnerRepo(ResourceOwnerMapper resourceOwnerMapper, DuplicateRecordExceptionFactory duplicateRecordExceptionFactory){
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

    @Override
    public ResourceOwner getByIdWithProfile(UUID id) throws RecordNotFoundException {
        ResourceOwner resourceOwner = resourceOwnerMapper.getByIdWithProfile(id);
        if (resourceOwner != null) {
            return resourceOwner;
        }

        throw new RecordNotFoundException("Resource Owner: " + id.toString());
    }

    @Override
    public ResourceOwner getByLocalToken(String token) throws RecordNotFoundException {
        ResourceOwner resourceOwner = resourceOwnerMapper.getByLocalToken(token);
        if (resourceOwner != null) {
            return resourceOwner;
        }

        throw new RecordNotFoundException("Resource Owner was not found");
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

    @Override
    public void updatePassword(UUID id, String password) {
        resourceOwnerMapper.updatePassword(id, password);
    }

    @Override
    public void updateEmail(UUID id, String email) {
        resourceOwnerMapper.updateEmail(id, email);
    }
}
