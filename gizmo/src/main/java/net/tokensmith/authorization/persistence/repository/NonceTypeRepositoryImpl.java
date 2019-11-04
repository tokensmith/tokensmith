package net.tokensmith.authorization.persistence.repository;


import net.tokensmith.repository.entity.NonceName;
import net.tokensmith.repository.entity.NonceType;
import net.tokensmith.authorization.persistence.mapper.NonceTypeMapper;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.repository.repo.NonceTypeRepository;
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
    public NonceType getByName(NonceName name) throws RecordNotFoundException {
        NonceType nonceType = nonceTypeMapper.getByName(name.toString());

        if (nonceType == null) {
            throw new RecordNotFoundException("NonceType was not found");
        }

        return nonceType;
    }
}
