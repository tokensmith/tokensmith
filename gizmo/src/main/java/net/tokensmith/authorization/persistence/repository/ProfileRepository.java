package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.authorization.persistence.entity.Profile;
import net.tokensmith.authorization.persistence.exceptions.RecordNotFoundException;

import java.util.UUID;

/**
 * Created by tommackenzie on 3/17/16.
 */
public interface ProfileRepository {
    void insert(Profile profile);
    Profile getByResourceOwnerId(UUID id) throws RecordNotFoundException;
}
