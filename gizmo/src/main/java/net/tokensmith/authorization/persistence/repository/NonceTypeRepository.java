package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.authorization.nonce.entity.NonceName;
import net.tokensmith.authorization.persistence.entity.NonceType;
import net.tokensmith.authorization.persistence.exceptions.RecordNotFoundException;

import java.util.UUID;

public interface NonceTypeRepository {
    void insert(NonceType nonceType);
    NonceType getById(UUID id) throws RecordNotFoundException;
    NonceType getByName(NonceName name) throws RecordNotFoundException;
}
