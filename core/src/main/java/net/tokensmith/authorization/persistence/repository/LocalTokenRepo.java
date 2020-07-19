package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.authorization.persistence.factory.DuplicateRecordExceptionFactory;
import net.tokensmith.authorization.persistence.mapper.LocalTokenMapper;
import net.tokensmith.repository.entity.LocalToken;
import net.tokensmith.repository.exceptions.DuplicateRecordException;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.repository.repo.LocalTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.UUID;

;

@Component
public class LocalTokenRepo implements LocalTokenRepository {
    private static String SCHEMA = "local_token";
    private LocalTokenMapper localTokenMapper;
    private DuplicateRecordExceptionFactory duplicateRecordExceptionFactory;

    @Autowired
    public LocalTokenRepo(LocalTokenMapper localTokenMapper, DuplicateRecordExceptionFactory duplicateRecordExceptionFactory) {
        this.localTokenMapper = localTokenMapper;
        this.duplicateRecordExceptionFactory = duplicateRecordExceptionFactory;
    }

    @Override
    public void insert(LocalToken token) throws DuplicateRecordException {
        try {
            localTokenMapper.insert(token);
        } catch (DuplicateKeyException e) {
            throw duplicateRecordExceptionFactory.make(e, SCHEMA);
        }
    }

    @Override
    public LocalToken getById(UUID id) throws RecordNotFoundException{
        LocalToken localToken = localTokenMapper.getById(id);
        if (localToken != null) {
            return localToken;
        }

        throw new RecordNotFoundException("Local Token: " + id.toString());
    }

    @Override
    public void revokeActive(UUID resourceOwnerId) {
        localTokenMapper.revokeActive(resourceOwnerId);
    }
}
