package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.AuthUser;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;

import java.util.UUID;

/**
 * Created by tommackenzie on 10/11/14.
 */
public interface AuthUserRepository {
    public AuthUser getByUUID(UUID uuid) throws RecordNotFoundException;
    public AuthUser getByEmailAndPassword(String email, byte[] password) throws RecordNotFoundException;
    public void insert(AuthUser authUser);
}
