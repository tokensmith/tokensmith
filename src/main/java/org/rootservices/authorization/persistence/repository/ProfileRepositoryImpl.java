package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.Profile;
import org.rootservices.authorization.persistence.mapper.ProfileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by tommackenzie on 3/13/16.
 */
@Component
public class ProfileRepositoryImpl implements ProfileRepository {

    private ProfileMapper profileMapper;

    @Autowired
    public ProfileRepositoryImpl(ProfileMapper profileMapper) {
        this.profileMapper = profileMapper;
    }

    @Override
    public void insert(Profile profile) {
        profileMapper.insert(profile);
    }
}
