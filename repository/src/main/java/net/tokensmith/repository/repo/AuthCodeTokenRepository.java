package net.tokensmith.repository.repo;

import net.tokensmith.repository.entity.AuthCodeToken;
import net.tokensmith.repository.exceptions.DuplicateRecordException;
import net.tokensmith.repository.exceptions.RecordNotFoundException;

import java.util.UUID;

/**
 * Created by tommackenzie on 4/16/16.
 */
public interface AuthCodeTokenRepository {
    void insert(AuthCodeToken authCodeToken) throws DuplicateRecordException;
    AuthCodeToken getByTokenId(UUID tokenId) throws RecordNotFoundException;
}
