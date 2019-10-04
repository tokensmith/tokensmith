package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;

import java.util.UUID;


public interface ResourceOwnerRepository {
    ResourceOwner getById(UUID id) throws RecordNotFoundException;
    ResourceOwner getByEmail(String email) throws RecordNotFoundException;
    ResourceOwner getByAccessToken(String accessToken) throws RecordNotFoundException;
    ResourceOwner getByAccessTokenWithProfileAndTokens(String accessToken) throws RecordNotFoundException;
    void insert(ResourceOwner resourceOwner) throws DuplicateRecordException;
    void setEmailVerified(UUID id);
    void updatePassword(UUID id, byte[] password);
}
