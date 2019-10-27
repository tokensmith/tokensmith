package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.authorization.persistence.entity.AuthCodeToken;
import net.tokensmith.authorization.persistence.exceptions.DuplicateRecordException;
import net.tokensmith.authorization.persistence.exceptions.RecordNotFoundException;

import java.util.UUID;

/**
 * Created by tommackenzie on 4/16/16.
 */
public interface AuthCodeTokenRepository {
    void insert(AuthCodeToken authCodeToken) throws DuplicateRecordException;
    AuthCodeToken getByTokenId(UUID tokenId) throws RecordNotFoundException;
}
