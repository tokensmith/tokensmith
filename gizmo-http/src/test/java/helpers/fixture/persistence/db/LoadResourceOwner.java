package helpers.fixture.persistence.db;

import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.repository.ResourceOwnerRepository;
import org.rootservices.authorization.security.ciphers.HashTextRandomSalt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by tommackenzie on 6/5/15.
 */
@Component
public class LoadResourceOwner {

    private HashTextRandomSalt textHasher;
    private ResourceOwnerRepository resourceOwnerRepository;

    @Autowired
    public LoadResourceOwner(HashTextRandomSalt textHasher, ResourceOwnerRepository resourceOwnerRepository) {
        this.textHasher = textHasher;
        this.resourceOwnerRepository = resourceOwnerRepository;
    }

    public ResourceOwner run() throws DuplicateRecordException {
        ResourceOwner ro = makeResourceOwner();
        resourceOwnerRepository.insert(ro);
        return ro;
    }

    protected ResourceOwner makeResourceOwner() {
        ResourceOwner ro = new ResourceOwner();
        ro.setId(UUID.randomUUID());
        ro.setEmail("test-" + UUID.randomUUID().toString() + "@rootservices.org");

        String hashedPassword = textHasher.run("password");
        ro.setPassword(hashedPassword.getBytes());

        return ro;
    }
}
