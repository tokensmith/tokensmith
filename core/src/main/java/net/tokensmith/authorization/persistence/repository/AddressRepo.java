package net.tokensmith.authorization.persistence.repository;


import net.tokensmith.repository.entity.Address;
import net.tokensmith.authorization.persistence.mapper.AddressMapper;
import net.tokensmith.repository.repo.AddressRepository;
import org.springframework.stereotype.Component;

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
}
