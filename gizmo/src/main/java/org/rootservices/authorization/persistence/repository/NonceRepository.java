package org.rootservices.authorization.persistence.repository;


import org.rootservices.authorization.nonce.entity.NonceName;
import org.rootservices.authorization.persistence.entity.Nonce;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;

import java.util.UUID;

public interface NonceRepository {
    void insert(Nonce nonce);
    Nonce getById(UUID id) throws RecordNotFoundException;
    Nonce getByTypeAndNonce(NonceName type, String nonce) throws RecordNotFoundException;
    Nonce getByNonce(String nonce) throws RecordNotFoundException;
    void revokeUnSpent(String type, UUID resourceOwnerId);
    void setSpent(UUID id);
}
