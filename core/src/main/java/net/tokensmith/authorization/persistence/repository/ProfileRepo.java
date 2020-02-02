package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.repository.entity.Profile;
import net.tokensmith.authorization.persistence.mapper.ProfileMapper;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.repository.repo.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by tommackenzie on 3/17/16.
 */
@Component
public class ProfileRepo implements ProfileRepository {
    private ProfileMapper profileMapper;

    @Autowired
    public ProfileRepo(ProfileMapper profileMapper) {
        this.profileMapper = profileMapper;
    }


    @Override
    public void insert(Profile profile) {
        profileMapper.insert(profile);
    }

    @Override
    public Profile getByResourceOwnerId(UUID id) throws RecordNotFoundException {
        Profile profile = profileMapper.getByResourceId(id);
        if (profile == null) {
            throw new RecordNotFoundException("Profile was not found");
        }
        return profile;
    }

    @Override
    public void update(UUID resourceOwnerId, Profile profile) {
        profileMapper.update(resourceOwnerId, profile);
    }
}
