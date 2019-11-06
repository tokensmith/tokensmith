package net.tokensmith.repository.repo;

import net.tokensmith.repository.entity.AccessRequest;

/**
 * Created by tommackenzie on 4/15/15.
 */
public interface AccessRequestRepository {
    void insert(AccessRequest authRequest);
}
