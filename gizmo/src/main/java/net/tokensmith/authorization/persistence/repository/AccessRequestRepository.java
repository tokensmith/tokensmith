package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.authorization.persistence.entity.AccessRequest;

/**
 * Created by tommackenzie on 4/15/15.
 */
public interface AccessRequestRepository {
    void insert(AccessRequest authRequest);
}
