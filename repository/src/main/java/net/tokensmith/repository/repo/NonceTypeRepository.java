package net.tokensmith.repository.repo;

import net.tokensmith.repository.entity.NonceName;
import net.tokensmith.repository.entity.NonceType;
import net.tokensmith.repository.exceptions.RecordNotFoundException;

import java.util.UUID;

public interface NonceTypeRepository {
    void insert(NonceType nonceType);
    NonceType getById(UUID id) throws RecordNotFoundException;
    NonceType getByName(NonceName name) throws RecordNotFoundException;
}
