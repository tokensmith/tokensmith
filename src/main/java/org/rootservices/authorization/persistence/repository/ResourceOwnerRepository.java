package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;

import java.util.UUID;

/**
 * Created by tommackenzie on 10/11/14.
 */
public interface ResourceOwnerRepository {
    ResourceOwner getByUUID(UUID uuid) throws RecordNotFoundException;
    ResourceOwner getByEmail(String email) throws RecordNotFoundException;
    void insert(ResourceOwner resourceOwner);
    ResourceOwner getByAccessToken(byte[] accessToken) throws RecordNotFoundException;
}
