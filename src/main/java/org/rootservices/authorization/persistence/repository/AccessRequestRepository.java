package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.AccessRequest;

/**
 * Created by tommackenzie on 4/15/15.
 */
public interface AccessRequestRepository {
    public void insert(AccessRequest authRequest);
}
