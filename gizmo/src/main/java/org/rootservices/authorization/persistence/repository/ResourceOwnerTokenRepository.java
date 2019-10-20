package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.ResourceOwnerToken;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;

/**
 * Created by tommackenzie on 4/28/16.
 */
public interface ResourceOwnerTokenRepository {
    ResourceOwnerToken getByAccessToken(String accessToken) throws RecordNotFoundException;
    void insert(ResourceOwnerToken resourceOwnerToken);
}
