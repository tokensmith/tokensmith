package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.NonceType;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;

import java.util.UUID;

public interface NonceTypeRepository {
    void insert(NonceType nonceType);
    NonceType getById(UUID id) throws RecordNotFoundException;
    NonceType getByName(String name) throws RecordNotFoundException;
}
