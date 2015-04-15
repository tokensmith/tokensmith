package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;

import java.util.UUID;

/**
 * Created by tommackenzie on 10/11/14.
 */
public interface ResourceOwnerRepository {
    public ResourceOwner getByUUID(UUID uuid) throws RecordNotFoundException;
    public ResourceOwner getByEmailAndPassword(String email, byte[] password) throws RecordNotFoundException;
    public void insert(ResourceOwner authUser);
}
