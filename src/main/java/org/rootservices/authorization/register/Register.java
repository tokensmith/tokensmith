package org.rootservices.authorization.register;

import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.repository.ResourceOwnerRepository;
import org.rootservices.authorization.security.HashTextRandomSalt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class Register {
    private ResourceOwnerRepository resourceOwnerRepository;
    private HashTextRandomSalt hashTextRandomSalt;

    @Autowired
    public Register(ResourceOwnerRepository resourceOwnerRepository, HashTextRandomSalt hashTextRandomSalt) {
        this.resourceOwnerRepository = resourceOwnerRepository;
        this.hashTextRandomSalt = hashTextRandomSalt;
    }

    public ResourceOwner run(String email, String password, String repeatPassword) throws RegisterException {

        validate(email, password, repeatPassword);

        ResourceOwner ro = new ResourceOwner(
                UUID.randomUUID(),
                email,
                hashTextRandomSalt.run(password).getBytes()
        );

        try {
            resourceOwnerRepository.insert(ro);
        } catch (DuplicateRecordException e) {
            RegisterError registerError = RegisterError.UNKNOWN;
            if (e.getKey().isPresent() && e.getKey().get().equals("email")) {
                registerError = RegisterError.EMAIL_TAKEN;
            }
            throw new RegisterException("Could not insert resource_owner", registerError, e);
        }

        // TODO: send off the welcome/confirmation email.

        return ro;
    }

    protected void validate(String email, String password, String repeatPassword) throws RegisterException {
        if (!hasValue(email)) {
            throw new RegisterException("Email is empty or null", RegisterError.EMAIL_MISSING);
        }

        if (!hasValue(password)) {
            throw new RegisterException("Password is empty or null", RegisterError.PASSWORD_MISSING);
        }

        if (!hasValue(repeatPassword)) {
            throw new RegisterException("Repeat password is empty or null", RegisterError.REPEAT_PASSWORD_MISSING);
        }

        if (!password.equals(repeatPassword)) {
            throw new RegisterException("Passwords do not match", RegisterError.PASSWORD_MISMATCH);
        }
    }

    protected Boolean hasValue(String value) {
        if (value == null || "".equals(value)) {
            return false;
        }
        return true;
    }
}
