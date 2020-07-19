package net.tokensmith.authorization.register;

import net.tokensmith.authorization.nonce.message.MessageKey;
import net.tokensmith.authorization.nonce.message.MessageType;
import net.tokensmith.authorization.register.exception.NonceException;
import net.tokensmith.authorization.register.exception.RegisterException;
import net.tokensmith.authorization.security.RandomString;
import net.tokensmith.authorization.security.ciphers.HashTextRandomSalt;
import net.tokensmith.authorization.security.ciphers.HashToken;
import net.tokensmith.pelican.Publish;
import net.tokensmith.repository.entity.Nonce;
import net.tokensmith.repository.entity.NonceName;
import net.tokensmith.repository.entity.NonceType;
import net.tokensmith.repository.entity.Profile;
import net.tokensmith.repository.entity.ResourceOwner;
import net.tokensmith.repository.exceptions.DuplicateRecordException;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.repository.repo.NonceRepository;
import net.tokensmith.repository.repo.NonceTypeRepository;
import net.tokensmith.repository.repo.ProfileRepository;
import net.tokensmith.repository.repo.ResourceOwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class Register {
    private ResourceOwnerRepository resourceOwnerRepository;
    private ProfileRepository profileRepository;
    private HashTextRandomSalt hashTextRandomSalt;
    private RandomString randomString;
    private HashToken hashToken;
    private NonceTypeRepository nonceTypeRepository;
    private NonceRepository nonceRepository;
    private Publish publish;

    private static String REGISTER_ERROR = "Could not insert resource_owner";
    private static String EMAIL_REQUIRED = "Email is empty or null";
    private static String PASSWORD_REQUIRED = "Password is empty or null";
    private static String REPEAT_PASSWORD_REQUIRED = "Repeat password is empty or null";
    private static String PASSWORD_MISMATCH = "Passwords do not match";
    private static String EMAIL_FIELD = "email";
    private static String EMPTY = "";

    @Autowired
    public Register(ResourceOwnerRepository resourceOwnerRepository, ProfileRepository profileRepository, HashTextRandomSalt hashTextRandomSalt, RandomString randomString, HashToken hashToken, NonceTypeRepository nonceTypeRepository, NonceRepository nonceRepository, Publish publish) {
        this.resourceOwnerRepository = resourceOwnerRepository;
        this.profileRepository = profileRepository;
        this.hashTextRandomSalt = hashTextRandomSalt;
        this.randomString = randomString;
        this.hashToken = hashToken;
        this.nonceTypeRepository = nonceTypeRepository;
        this.nonceRepository = nonceRepository;
        this.publish = publish;
    }

    public ResourceOwner run(String email, String password, String repeatPassword, String baseURI) throws RegisterException, NonceException {

        validate(email, password, repeatPassword);

        String hashedPassword = hashTextRandomSalt.run(password);
        ResourceOwner ro = new ResourceOwner(UUID.randomUUID(), email, hashedPassword);

        try {
            resourceOwnerRepository.insert(ro);
        } catch (DuplicateRecordException e) {
            RegisterError registerError = makeRegisterError(e);
            throw new RegisterException(REGISTER_ERROR, registerError, e);
        }

        Profile profile = new Profile();
        profile.setId(UUID.randomUUID());
        profile.setResourceOwnerId(ro.getId());
        profile.setPhoneNumberVerified(false);

        profileRepository.insert(profile);

        // insert nonce used to validate user's email address.
        String plainTextNonce = randomString.run();
        String hashedNonce = hashToken.run(plainTextNonce);
        insertNonce(ro, hashedNonce);

        Map<String, String> msg = new HashMap<>();
        msg.put(MessageKey.TYPE.toString(), MessageType.WELCOME.toString());
        msg.put(MessageKey.RECIPIENT.toString(), ro.getEmail());
        msg.put(MessageKey.BASE_LINK.toString(), baseURI + "/welcome?nonce=");
        msg.put(MessageKey.NONCE.toString(), plainTextNonce);

        publish.send("message-user", msg);

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

    protected Nonce insertNonce(ResourceOwner ro, String hashedNonce) throws NonceException {
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
