package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.AuthRequest;

/**
 * Created by tommackenzie on 4/15/15.
 */
public interface AuthRequestRepository {
    public void insert(AuthRequest authRequest);
}
