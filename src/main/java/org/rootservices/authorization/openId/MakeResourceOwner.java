package org.rootservices.authorization.openId;

import org.rootservices.authorization.persistence.entity.Profile;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.repository.ProfileRepository;
import org.rootservices.authorization.persistence.repository.ResourceOwnerRepository;
import org.rootservices.authorization.security.HashTextRandomSalt;
import org.rootservices.authorization.security.HashTextStaticSalt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MakeResourceOwner {
    private ResourceOwnerRepository resourceOwnerRepository;
    private HashTextRandomSalt hashTextRandomSalt;

    @Autowired
    public MakeResourceOwner(ResourceOwnerRepository resourceOwnerRepository, HashTextRandomSalt hashTextRandomSalt) {
        this.resourceOwnerRepository = resourceOwnerRepository;
        this.hashTextRandomSalt = hashTextRandomSalt;
    }

    public ResourceOwner make(String email, String password) throws DuplicateRecordException {
        ResourceOwner ro = new ResourceOwner(
                UUID.randomUUID(),
                email,
                hashTextRandomSalt.run(password).getBytes()
        );
        // this could cause duplicate record.
        resourceOwnerRepository.insert(ro);

        // TODO: send off the welcome/confirmation email.

        return ro;
    }
}
