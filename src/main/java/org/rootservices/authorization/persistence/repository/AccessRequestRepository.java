package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.AccessRequest;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;

import java.util.UUID;

/**
 * Created by tommackenzie on 4/15/15.
 */
public interface AccessRequestRepository {
    void insert(AccessRequest authRequest);
    AccessRequest getByAccessToken(String accessToken) throws RecordNotFoundException;
}
