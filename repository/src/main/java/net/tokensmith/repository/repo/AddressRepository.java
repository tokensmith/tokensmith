package net.tokensmith.repository.repo;

import net.tokensmith.repository.entity.Address;
import net.tokensmith.repository.exceptions.RecordNotFoundException;

import java.util.UUID;

/**
 * Created by tommackenzie on 5/11/17.
 */
public interface AddressRepository {
    void insert(Address address);
    Address getByIdAndResourceOwnerId(UUID id, UUID resourceOwnerId) throws RecordNotFoundException;
    void update(UUID resourceOwnerId, Address address);
    void delete(UUID id, UUID resourceOwnerId);
}
