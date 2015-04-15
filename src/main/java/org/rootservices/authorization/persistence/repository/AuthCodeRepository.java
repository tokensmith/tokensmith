package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.AuthCode;

/**
 * Created by tommackenzie on 4/10/15.
 */
public interface AuthCodeRepository {
    public void insert(AuthCode authCode);
}
