package net.tokensmith.authorization.nonce;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import net.tokensmith.authorization.nonce.entity.NonceName;
import net.tokensmith.authorization.persistence.entity.Nonce;
import net.tokensmith.authorization.persistence.entity.NonceType;
import net.tokensmith.authorization.persistence.entity.ResourceOwner;
import net.tokensmith.authorization.persistence.exceptions.RecordNotFoundException;
import net.tokensmith.authorization.persistence.repository.NonceRepository;
import net.tokensmith.authorization.persistence.repository.NonceTypeRepository;
import net.tokensmith.authorization.persistence.repository.ResourceOwnerRepository;
import net.tokensmith.authorization.register.exception.NonceException;
import net.tokensmith.authorization.security.RandomString;
import net.tokensmith.authorization.security.ciphers.HashToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class InsertNonce {
    private static final Logger logger = LogManager.getLogger(InsertNonce.class);
    private ResourceOwnerRepository resourceOwnerRepository;
    private RandomString randomString;
    private HashToken hashToken;
    private NonceTypeRepository nonceTypeRepository;
    private NonceRepository nonceRepository;


    @Autowired
    public InsertNonce(ResourceOwnerRepository resourceOwnerRepository, RandomString randomString, HashToken hashToken, NonceTypeRepository nonceTypeRepository, NonceRepository nonceRepository) {
        this.resourceOwnerRepository = resourceOwnerRepository;
        this.randomString = randomString;
        this.hashToken = hashToken;
        this.nonceTypeRepository = nonceTypeRepository;
        this.nonceRepository = nonceRepository;
    }

    public String insert(String email, NonceName nonceName) throws NonceException {
        ResourceOwner ro;
        try {
            ro = resourceOwnerRepository.getByEmail(email);
        } catch (RecordNotFoundException e) {
            throw new NonceException("Could not find resource owner to associate to nonce", e);
        }

        String plainTextNonce = insert(ro, nonceName);
        return plainTextNonce;
    }

    public String insert(ResourceOwner ro, NonceName nonceName) throws NonceException {

        String plainTextNonce = randomString.run();
        String hashedNonce = hashToken.run(plainTextNonce);
        insertNonce(ro, hashedNonce, nonceName);

        return plainTextNonce;
    }

    protected Nonce insertNonce(ResourceOwner ro, String hashedNonce, NonceName nonceName) throws NonceException {
        NonceType nonceType;
        try {
            nonceType = nonceTypeRepository.getByName(nonceName);
        } catch (RecordNotFoundException e) {
            throw new NonceException("Could not find nonce type to associate nonce", e);
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
