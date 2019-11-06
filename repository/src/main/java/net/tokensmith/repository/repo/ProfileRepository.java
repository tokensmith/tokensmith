package net.tokensmith.repository.repo;

import net.tokensmith.repository.entity.Profile;
import net.tokensmith.repository.exceptions.RecordNotFoundException;

import java.util.UUID;

/**
 * Created by tommackenzie on 3/17/16.
 */
public interface ProfileRepository {
    void insert(Profile profile);
    Profile getByResourceOwnerId(UUID id) throws RecordNotFoundException;
}
