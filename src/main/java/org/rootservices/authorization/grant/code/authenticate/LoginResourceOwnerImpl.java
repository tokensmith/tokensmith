package org.rootservices.authorization.grant.code.authenticate;

import org.rootservices.authorization.grant.code.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.grant.code.constant.ErrorCode;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ResourceOwnerRepository;
import org.rootservices.authorization.security.Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by tommackenzie on 4/12/15.
 */
@Component
public class LoginResourceOwnerImpl implements LoginResourceOwner {

    @Autowired
    private Hash hash;

    @Autowired
    private ResourceOwnerRepository resourceOwnerRepository;

    public LoginResourceOwnerImpl() {}

    public LoginResourceOwnerImpl(Hash hash, ResourceOwnerRepository resourceOwnerRepository) {
        this.hash = hash;
        this.resourceOwnerRepository = resourceOwnerRepository;
    }

    @Override
    public UUID run(String userName, String plainTextPassword) throws UnauthorizedException {
        String hashedPassword = hash.run(plainTextPassword);
        ResourceOwner resourceOwner = null;

        try {

            resourceOwner = resourceOwnerRepository.getByEmailAndPassword(
                    userName, hashedPassword.getBytes()
            );

        } catch (RecordNotFoundException e) {

            throw new UnauthorizedException(
                    "Resource Owner not found",
                    e, ErrorCode.UNAUTHORIZED.getCode());

        }

        return resourceOwner.getUuid();
    }
}
