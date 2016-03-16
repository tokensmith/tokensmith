package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.Profile;

/**
 * Created by tommackenzie on 3/13/16.
 */
public interface ProfileRepository {
    void insert(Profile profile);
}
