package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.AuthCodeToken;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;

/**
 * Created by tommackenzie on 4/16/16.
 */
public interface AuthCodeTokenRepository {
    void insert(AuthCodeToken authCodeToken) throws DuplicateRecordException;
}
