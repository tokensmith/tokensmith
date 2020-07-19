package net.tokensmith.authorization.site;

import net.tokensmith.repository.entity.Address;
import net.tokensmith.repository.repo.AddressRepository;
import net.tokensmith.repository.repo.ProfileRepository;
import net.tokensmith.repository.repo.ResourceOwnerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;


public class ProfileManger {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileManger.class);
    private ResourceOwnerRepository resourceOwnerRepository;
    private ProfileRepository profileRepository;
    private AddressRepository addressRepository;

    @Autowired
    public ProfileManger(ResourceOwnerRepository resourceOwnerRepository) {
        this.resourceOwnerRepository = resourceOwnerRepository;
    }

    public void addAddress() {

    }

    public Address getAddress(UUID id) {
        return null;
    }

    public void updateAddress() {

    }

    public void deleteAddress(UUID id) {

    }
}
