package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.Address;

/**
 * Created by tommackenzie on 5/11/17.
 */
public interface AddressRepository {
    void insert(Address address);
}
