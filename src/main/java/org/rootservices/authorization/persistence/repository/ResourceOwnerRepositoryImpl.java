package org.rootservices.authorization.persistence.repository;


import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.mapper.ResourceOwnerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by tommackenzie on 10/11/14.
 */
@Component
public class ResourceOwnerRepositoryImpl implements ResourceOwnerRepository {

    @Autowired
    ResourceOwnerMapper resourceOwnerMapper;

    public ResourceOwnerRepositoryImpl() {}

    public ResourceOwnerRepositoryImpl(ResourceOwnerMapper authUserMapper){
        this.resourceOwnerMapper = authUserMapper;
    }

    public ResourceOwner getByUUID(UUID uuid) throws RecordNotFoundException {
        ResourceOwner authUser = resourceOwnerMapper.getByUUID(uuid);
        if (authUser != null) {
            return authUser;
        }

        throw new RecordNotFoundException("AuthUser: " + uuid.toString());
    }

    public ResourceOwner getByEmail(String email) throws RecordNotFoundException {
        ResourceOwner authUser = resourceOwnerMapper.getByEmail(email);
        if (authUser != null) {
            return authUser;
        }

        throw new RecordNotFoundException("AuthUser: " + email);
    }

    public void insert(ResourceOwner resourceOwner) {
        resourceOwnerMapper.insert(resourceOwner);
    }
}
