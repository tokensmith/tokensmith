package net.tokensmith.authorization.persistence.repository;


import net.tokensmith.authorization.nonce.entity.NonceName;
import net.tokensmith.authorization.persistence.entity.Nonce;
import net.tokensmith.authorization.persistence.exceptions.RecordNotFoundException;

import java.util.UUID;

public interface NonceRepository {
    void insert(Nonce nonce);
    Nonce getById(UUID id) throws RecordNotFoundException;
    Nonce getByTypeAndNonce(NonceName type, String nonce) throws RecordNotFoundException;
    Nonce getByNonce(String nonce) throws RecordNotFoundException;
    void revokeUnSpent(String type, UUID resourceOwnerId);
    void setSpent(UUID id);
}
