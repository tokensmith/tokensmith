package org.rootservices.authorization.register;

import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.repository.ResourceOwnerRepository;
import org.rootservices.authorization.security.HashTextRandomSalt;
import org.rootservices.pelican.Publish;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class Register {
    private ResourceOwnerRepository resourceOwnerRepository;
    private HashTextRandomSalt hashTextRandomSalt;
    private Publish publish;
    private String issuer;

    private static String REGISTER_ERROR = "Could not insert resource_owner";
    private static String EMAIL_REQUIRED = "Email is empty or null";
    private static String PASSWORD_REQUIRED = "Password is empty or null";
    private static String REPEAT_PASSWORD_REQUIRED = "Repeat password is empty or null";
    private static String PASSWORD_MISMATCH = "Passwords do not match";
    private static String EMAIL_FIELD = "email";
    private static String EMPTY = "";

    @Autowired
    public Register(ResourceOwnerRepository resourceOwnerRepository, HashTextRandomSalt hashTextRandomSalt, Publish publish, String issuer) {
        this.resourceOwnerRepository = resourceOwnerRepository;
        this.hashTextRandomSalt = hashTextRandomSalt;
        this.publish = publish;
        this.issuer = issuer;
    }

    public ResourceOwner run(String email, String password, String repeatPassword) throws RegisterException {

        validate(email, password, repeatPassword);

        byte[] hashedPassword = hashTextRandomSalt.run(password).getBytes();
        ResourceOwner ro = new ResourceOwner(UUID.randomUUID(), email, hashedPassword);

        try {
            resourceOwnerRepository.insert(ro);
        } catch (DuplicateRecordException e) {
            RegisterError registerError = makeRegisterError(e);
            throw new RegisterException(REGISTER_ERROR, registerError, e);
        }

        Map<String, String> msg = new HashMap<>();
        msg.put("type", "welcome");
        msg.put("recipient", ro.getEmail());
        msg.put("body_link", issuer + "/welcome");

        publish.send("mailer", msg);

        return ro;
    }

    protected RegisterError makeRegisterError(DuplicateRecordException e) {
        RegisterError registerError = RegisterError.UNKNOWN;
        if (e.getKey().isPresent() && EMAIL_FIELD.equals(e.getKey().get())) {
            registerError = RegisterError.EMAIL_TAKEN;
        }
        return registerError;
    }

    protected void validate(String email, String password, String repeatPassword) throws RegisterException {
        if (!hasValue(email)) {
            throw new RegisterException(EMAIL_REQUIRED, RegisterError.EMAIL_MISSING);
        }

        if (!hasValue(password)) {
            throw new RegisterException(PASSWORD_REQUIRED, RegisterError.PASSWORD_MISSING);
        }

        if (!hasValue(repeatPassword)) {
            throw new RegisterException(REPEAT_PASSWORD_REQUIRED, RegisterError.REPEAT_PASSWORD_MISSING);
        }

        if (!password.equals(repeatPassword)) {
            throw new RegisterException(PASSWORD_MISMATCH, RegisterError.PASSWORD_MISMATCH);
        }
    }

    protected Boolean hasValue(String value) {
        if (value == null || EMPTY.equals(value)) {
            return false;
        }
        return true;
    }
}
