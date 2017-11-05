package org.rootservices.authorization.register;

import org.rootservices.authorization.nonce.message.MessageKey;
import org.rootservices.authorization.nonce.message.MessageType;
import org.rootservices.authorization.nonce.message.Topic;
import org.rootservices.authorization.nonce.entity.NonceName;
import org.rootservices.authorization.persistence.entity.Nonce;
import org.rootservices.authorization.persistence.entity.NonceType;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.authorization.persistence.exceptions.DuplicateRecordException;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.NonceRepository;
import org.rootservices.authorization.persistence.repository.NonceTypeRepository;
import org.rootservices.authorization.persistence.repository.ResourceOwnerRepository;
import org.rootservices.authorization.register.exception.NonceException;
import org.rootservices.authorization.register.exception.RegisterException;
import org.rootservices.authorization.security.RandomString;
import org.rootservices.authorization.security.ciphers.HashTextRandomSalt;
import org.rootservices.authorization.security.ciphers.HashTextStaticSalt;
import org.rootservices.pelican.Publish;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class Register {
    private ResourceOwnerRepository resourceOwnerRepository;
    private HashTextRandomSalt hashTextRandomSalt;
    private RandomString randomString;
    private HashTextStaticSalt hashTextStaticSalt;
    private NonceTypeRepository nonceTypeRepository;
    private NonceRepository nonceRepository;
    private Publish publish;
    private String issuer;

    private static String REGISTER_ERROR = "Could not insert resource_owner";
    private static String EMAIL_REQUIRED = "Email is empty or null";
    private static String PASSWORD_REQUIRED = "Password is empty or null";
    private static String REPEAT_PASSWORD_REQUIRED = "Repeat password is empty or null";
    private static String PASSWORD_MISMATCH = "Passwords do not match";
    private static String EMAIL_FIELD = "email";
    private static String EMPTY = "";
    private static String NONCE_TYPE = "welcome";

    @Autowired
    public Register(ResourceOwnerRepository resourceOwnerRepository, HashTextRandomSalt hashTextRandomSalt, RandomString randomString, HashTextStaticSalt hashTextStaticSalt, NonceTypeRepository nonceTypeRepository, NonceRepository nonceRepository, Publish publish, String issuer) {
        this.resourceOwnerRepository = resourceOwnerRepository;
        this.hashTextRandomSalt = hashTextRandomSalt;
        this.randomString = randomString;
        this.hashTextStaticSalt = hashTextStaticSalt;
        this.nonceTypeRepository = nonceTypeRepository;
        this.nonceRepository = nonceRepository;
        this.publish = publish;
        this.issuer = issuer;
    }

    public ResourceOwner run(String email, String password, String repeatPassword) throws RegisterException, NonceException {

        validate(email, password, repeatPassword);

        byte[] hashedPassword = hashTextRandomSalt.run(password).getBytes();
        ResourceOwner ro = new ResourceOwner(UUID.randomUUID(), email, hashedPassword);

        try {
            resourceOwnerRepository.insert(ro);
        } catch (DuplicateRecordException e) {
            RegisterError registerError = makeRegisterError(e);
            throw new RegisterException(REGISTER_ERROR, registerError, e);
        }

        // insert nonce used to validate user's email address.
        String plainTextNonce = randomString.run();
        byte[] hashedNonce = hashTextStaticSalt.run(plainTextNonce).getBytes();
        insertNonce(ro, hashedNonce);

        Map<String, String> msg = new HashMap<>();
        msg.put(MessageKey.TYPE.toString(), MessageType.WELCOME.toString());
        msg.put(MessageKey.RECIPIENT.toString(), ro.getEmail());
        msg.put(MessageKey.BASE_LINK.toString(), issuer + "/welcome?nonce=");
        msg.put(MessageKey.NONCE.toString(), plainTextNonce);

        publish.send(Topic.MAILER.toString(), msg);

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

    protected Nonce insertNonce(ResourceOwner ro, byte[] hashedNonce) throws NonceException {
        NonceType nonceType;
        try {
            nonceType = nonceTypeRepository.getByName(NonceName.WELCOME);
        } catch (RecordNotFoundException e) {
            throw new NonceException("Fatal - could not find nonce type for welcome nonce", e);
        }

        Nonce nonce = new Nonce();
        nonce.setId(UUID.randomUUID());
        nonce.setResourceOwner(ro);
        nonce.setNonceType(nonceType);
        nonce.setNonce(hashedNonce);
        nonce.setExpiresAt(OffsetDateTime.now().plusSeconds(nonceType.getSecondsToExpiry()));

        nonceRepository.insert(nonce);

        return nonce;
    }
}
