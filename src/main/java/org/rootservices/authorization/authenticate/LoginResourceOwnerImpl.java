package org.rootservices.authorization.authenticate;

import org.rootservices.authorization.authenticate.exception.UnauthorizedException;
import org.rootservices.authorization.grant.code.constant.ErrorCode;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.ResourceOwnerRepository;
import org.rootservices.authorization.security.IsTextEqualToHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * Created by tommackenzie on 4/12/15.
 *
 * Section 4.1.1
 *
 *     "If the request is valid, the authorization server authenticates the resource
 *      owner and obtains an authorization decision (by asking the resource owner or by
 *      establishing approval via other means)."
 *
 */
@Component
public class LoginResourceOwnerImpl implements LoginResourceOwner {

    private MatchPasswords matchPasswords;
    private ResourceOwnerRepository resourceOwnerRepository;

    public LoginResourceOwnerImpl() {}

    @Autowired
    public LoginResourceOwnerImpl(MatchPasswords matchPasswords, ResourceOwnerRepository resourceOwnerRepository) {
        this.matchPasswords = matchPasswords;
        this.resourceOwnerRepository = resourceOwnerRepository;
    }

    @Override
    public UUID run(String userName, String plainTextPassword) throws UnauthorizedException {

        ResourceOwner resourceOwner = null;
        try {
            resourceOwner = resourceOwnerRepository.getByEmail(userName);
        } catch (RecordNotFoundException e) {
            throw new UnauthorizedException(
                ErrorCode.RESOURCE_OWNER_NOT_FOUND.getMessage(),
                e, ErrorCode.RESOURCE_OWNER_NOT_FOUND.getCode());
        }

        boolean passwordsMatch = matchPasswords.run(
            plainTextPassword, resourceOwner.getPassword()
        );

        return resourceOwner.getUuid();
    }
}
