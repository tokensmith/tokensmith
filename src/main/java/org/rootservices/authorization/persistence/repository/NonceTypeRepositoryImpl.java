package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.NonceType;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.mapper.NonceTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class NonceTypeRepositoryImpl implements NonceTypeRepository {

    private NonceTypeMapper nonceTypeMapper;

    @Autowired
    public NonceTypeRepositoryImpl(NonceTypeMapper nonceTypeMapper) {
        this.nonceTypeMapper = nonceTypeMapper;
    }

    @Override
    public void insert(NonceType nonceType) {
        nonceTypeMapper.insert(nonceType);
    }

    @Override
    public NonceType getById(UUID id) throws RecordNotFoundException {
        NonceType nonceType = nonceTypeMapper.getById(id);

        if (nonceType == null) {
            throw new RecordNotFoundException("NonceType was not found");
        }

        return nonceType;
    }

    @Override
    public NonceType getByName(String name) throws RecordNotFoundException {
        NonceType nonceType = nonceTypeMapper.getByName(name);

        if (nonceType == null) {
            throw new RecordNotFoundException("NonceType was not found");
        }

        return nonceType;
    }
}
