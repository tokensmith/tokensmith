package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.Nonce;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.mapper.NonceMapper;
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
    public Nonce getByNonce(String type, String nonce) throws RecordNotFoundException {
        Nonce nonceRecord = nonceMapper.getByNonce(type, nonce);

        if (nonceRecord == null) {
            throw new RecordNotFoundException("NonceType was not found");
        }

        return nonceRecord;
    }

    @Override
    public void setSpent(UUID id) {
        nonceMapper.setSpent(id);
    }
}
