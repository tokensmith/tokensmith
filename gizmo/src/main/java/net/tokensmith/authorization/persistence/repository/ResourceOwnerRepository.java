package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.authorization.persistence.entity.ResourceOwner;
import net.tokensmith.authorization.persistence.exceptions.DuplicateRecordException;
import net.tokensmith.authorization.persistence.exceptions.RecordNotFoundException;

import java.util.UUID;


public interface ResourceOwnerRepository {
    ResourceOwner getById(UUID id) throws RecordNotFoundException;
    ResourceOwner getByEmail(String email) throws RecordNotFoundException;
    ResourceOwner getByAccessToken(String accessToken) throws RecordNotFoundException;
    ResourceOwner getByAccessTokenWithProfileAndTokens(String accessToken) throws RecordNotFoundException;
    void insert(ResourceOwner resourceOwner) throws DuplicateRecordException;
    void setEmailVerified(UUID id);
    void updatePassword(UUID id, String password);
}
