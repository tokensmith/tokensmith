package org.rootservices.authorization.persistence.repository;


import org.rootservices.authorization.persistence.entity.AuthUser;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.mapper.AuthUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Created by tommackenzie on 10/11/14.
 */
@Repository
public class AuthUserRepositoryImpl implements AuthUserRepository {

    @Autowired
    AuthUserMapper authUserMapper;

    public AuthUser getByUUID(UUID uuid) throws RecordNotFoundException {
        AuthUser authUser = authUserMapper.getByUUID(uuid);
        if (authUser != null) {
            return authUser;
        }

        throw new RecordNotFoundException("AuthUser: " + uuid.toString());
    }

    public AuthUser getByEmailAndPassword(String email, byte[] password) throws RecordNotFoundException {
        AuthUser authUser = authUserMapper.getByEmailAndPassword(email, password);
        if (authUser != null) {
            return authUser;
        }

        throw new RecordNotFoundException("AuthUser: " + email);
    }

    public void insert(AuthUser authUser) {
        authUserMapper.insert(authUser);
    }
}
