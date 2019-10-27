package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.authorization.persistence.entity.Address;

/**
 * Created by tommackenzie on 5/11/17.
 */
public interface AddressRepository {
    void insert(Address address);
}
