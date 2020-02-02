package net.tokensmith.authorization.persistence.repository;


import net.tokensmith.repository.entity.Address;
import net.tokensmith.authorization.persistence.mapper.AddressMapper;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.repository.repo.AddressRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AddressRepo implements AddressRepository {
    private AddressMapper addressMapper;

    public AddressRepo(AddressMapper addressMapper) {
        this.addressMapper = addressMapper;
    }

    @Override
    public void insert(Address address) {
        addressMapper.insert(address);
    }

    @Override
    public Address getByIdAndResourceOwnerId(UUID id, UUID resourceOwnerId) throws RecordNotFoundException {
        Address address = addressMapper.getByIdAndResourceOwnerId(id, resourceOwnerId);
        if (address == null) {
            throw new RecordNotFoundException(
                String.format(
                    "Address not found for address id: %s, resource owner id: %s",
                    id, resourceOwnerId
                )
            );
        }
        return address;
    }

    @Override
    public void update(UUID resourceOwnerId, Address address) {
        addressMapper.update(resourceOwnerId, address);
    }

    @Override
    public void delete(UUID id, UUID resourceOwnerId) {
        addressMapper.delete(id, resourceOwnerId);
    }
}
