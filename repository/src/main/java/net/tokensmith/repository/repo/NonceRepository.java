package net.tokensmith.repository.repo;


import net.tokensmith.repository.entity.NonceName;
import net.tokensmith.repository.entity.Nonce;
import net.tokensmith.repository.exceptions.RecordNotFoundException;

import java.util.UUID;

public interface NonceRepository {
    void insert(Nonce nonce);
    Nonce getById(UUID id) throws RecordNotFoundException;
    Nonce getByTypeAndNonce(NonceName type, String nonce) throws RecordNotFoundException;
    Nonce getByNonce(String nonce) throws RecordNotFoundException;
    void revokeUnSpent(String type, UUID resourceOwnerId);
    void setSpent(UUID id);
}
