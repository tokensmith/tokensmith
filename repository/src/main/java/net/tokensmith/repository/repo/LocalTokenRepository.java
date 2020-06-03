package net.tokensmith.repository.repo;

import net.tokensmith.repository.entity.LocalToken;
import net.tokensmith.repository.exceptions.DuplicateRecordException;
import net.tokensmith.repository.exceptions.RecordNotFoundException;

import java.util.UUID;

public interface LocalTokenRepository {
    void insert(LocalToken token) throws DuplicateRecordException;
    LocalToken getById(UUID id) throws RecordNotFoundException;
    void revokeActive(UUID resourceOwnerId);
}
