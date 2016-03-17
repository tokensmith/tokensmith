package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.Profile;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;

import java.util.UUID;

/**
 * Created by tommackenzie on 3/17/16.
 */
public interface ProfileRepository {
    Profile getByResourceOwnerId(UUID id) throws RecordNotFoundException;
}
