package org.rootservices.authorization.persistence.repository;


import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.mapper.ResourceOwnerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Created by tommackenzie on 10/11/14.
 */
@Component
public class ResourceOwnerRepositoryImpl implements ResourceOwnerRepository {

    @Autowired
    ResourceOwnerMapper authUserMapper;

    public ResourceOwnerRepositoryImpl() {}

    public ResourceOwnerRepositoryImpl(ResourceOwnerMapper authUserMapper){
        this.authUserMapper = authUserMapper;
    }

    public ResourceOwner getByUUID(UUID uuid) throws RecordNotFoundException {
        ResourceOwner authUser = authUserMapper.getByUUID(uuid);
        if (authUser != null) {
            return authUser;
        }

        throw new RecordNotFoundException("AuthUser: " + uuid.toString());
    }

    public ResourceOwner getByEmailAndPassword(String email, byte[] password) throws RecordNotFoundException {
        ResourceOwner authUser = authUserMapper.getByEmailAndPassword(email, password);
        if (authUser != null) {
            return authUser;
        }

        throw new RecordNotFoundException("AuthUser: " + email);
    }

    public void insert(ResourceOwner authUser) {
        authUserMapper.insert(authUser);
    }
}
