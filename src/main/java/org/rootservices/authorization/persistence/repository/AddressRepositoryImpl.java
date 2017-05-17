package org.rootservices.authorization.persistence.repository;


import org.rootservices.authorization.persistence.entity.Address;
import org.rootservices.authorization.persistence.mapper.AddressMapper;
import org.springframework.stereotype.Component;

@Component
public class AddressRepositoryImpl implements AddressRepository {
    private AddressMapper addressMapper;

    public AddressRepositoryImpl(AddressMapper addressMapper) {
        this.addressMapper = addressMapper;
    }

    @Override
    public void insert(Address address) {
        addressMapper.insert(address);
    }
}
