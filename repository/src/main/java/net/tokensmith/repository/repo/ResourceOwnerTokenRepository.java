package net.tokensmith.repository.repo;

import net.tokensmith.repository.entity.ResourceOwnerToken;
import net.tokensmith.repository.exceptions.RecordNotFoundException;

/**
 * Created by tommackenzie on 4/28/16.
 */
public interface ResourceOwnerTokenRepository {
    ResourceOwnerToken getByAccessToken(String accessToken) throws RecordNotFoundException;
    void insert(ResourceOwnerToken resourceOwnerToken);
}
