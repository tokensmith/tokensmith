package net.tokensmith.authorization.persistence.repository;


import net.tokensmith.repository.entity.Nonce;
import net.tokensmith.authorization.persistence.mapper.NonceMapper;
import net.tokensmith.repository.entity.NonceName;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.repository.repo.NonceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class NonceRepositoryImpl implements NonceRepository {

    private NonceMapper nonceMapper;

    @Autowired
    public NonceRepositoryImpl(NonceMapper nonceMapper) {
        this.nonceMapper = nonceMapper;
    }

    @Override
    public void insert(Nonce nonce) {
        nonceMapper.insert(nonce);
    }

    @Override
    public Nonce getById(UUID id) throws RecordNotFoundException {
        Nonce nonce = nonceMapper.getById(id);

        if (nonce == null) {
            throw new RecordNotFoundException("NonceType was not found");
        }

        return nonce;
    }

    @Override
    public Nonce getByTypeAndNonce(NonceName type, String nonce) throws RecordNotFoundException {
        Nonce nonceRecord = nonceMapper.getByTypeAndNonce(type.toString().toLowerCase(), nonce);

        if (nonceRecord == null) {
            throw new RecordNotFoundException("Nonce was not found");
        }

        return nonceRecord;
    }

    @Override
    public Nonce getByNonce(String nonce) throws RecordNotFoundException {
        Nonce nonceRecord = nonceMapper.getByNonce(nonce);

        if (nonceRecord == null) {
            throw new RecordNotFoundException("Nonce was not found");
        }

        return nonceRecord;
    }

    @Override
    public void revokeUnSpent(String type, UUID resourceOwnerId) {
        nonceMapper.revokeUnSpent(type, resourceOwnerId);
    }

    @Override
    public void setSpent(UUID id) {
        nonceMapper.setSpent(id);
    }
}
