package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.authorization.persistence.entity.ResourceOwnerToken;
import net.tokensmith.authorization.persistence.exceptions.RecordNotFoundException;

/**
 * Created by tommackenzie on 4/28/16.
 */
public interface ResourceOwnerTokenRepository {
    ResourceOwnerToken getByAccessToken(String accessToken) throws RecordNotFoundException;
    void insert(ResourceOwnerToken resourceOwnerToken);
}
