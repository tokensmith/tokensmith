package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.Profile;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.mapper.ProfileMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

/**
 * Created by tommackenzie on 3/17/16.
 */
public class ProfileRepositoryImpl implements ProfileRepository {
    private ProfileMapper profileMapper;

    @Autowired
    public ProfileRepositoryImpl(ProfileMapper profileMapper) {
        this.profileMapper = profileMapper;
    }

    @Override
    public Profile getByResourceOwnerId(UUID id) throws RecordNotFoundException {
        Profile profile = profileMapper.getByResourceId(id);
        if (profile == null) {
            throw new RecordNotFoundException("Profile was not found");
        }
        return profile;
    }
}
