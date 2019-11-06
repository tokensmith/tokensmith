package net.tokensmith.repository.repo;

import net.tokensmith.repository.entity.ResourceOwner;
import net.tokensmith.repository.exceptions.DuplicateRecordException;
import net.tokensmith.repository.exceptions.RecordNotFoundException;

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
