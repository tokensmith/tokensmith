package org.rootservices.authorization.nonce;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.authorization.nonce.entity.NonceName;
import org.rootservices.authorization.persistence.entity.Nonce;
import org.rootservices.authorization.persistence.entity.NonceType;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.NonceRepository;
import org.rootservices.authorization.persistence.repository.NonceTypeRepository;
import org.rootservices.authorization.persistence.repository.ResourceOwnerRepository;
import org.rootservices.authorization.register.exception.NonceException;
import org.rootservices.authorization.security.RandomString;
import org.rootservices.authorization.security.ciphers.HashTextStaticSalt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class InsertNonce {
    private static final Logger logger = LogManager.getLogger(InsertNonce.class);
    private ResourceOwnerRepository resourceOwnerRepository;
    private RandomString randomString;
    private HashTextStaticSalt hashTextStaticSalt;
    private NonceTypeRepository nonceTypeRepository;
    private NonceRepository nonceRepository;


    @Autowired
    public InsertNonce(ResourceOwnerRepository resourceOwnerRepository, RandomString randomString, HashTextStaticSalt hashTextStaticSalt, NonceTypeRepository nonceTypeRepository, NonceRepository nonceRepository) {
        this.resourceOwnerRepository = resourceOwnerRepository;
        this.randomString = randomString;
        this.hashTextStaticSalt = hashTextStaticSalt;
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
        byte[] hashedNonce = hashTextStaticSalt.run(plainTextNonce).getBytes();
        insertNonce(ro, hashedNonce, nonceName);

        return plainTextNonce;
    }

    protected Nonce insertNonce(ResourceOwner ro, byte[] hashedNonce, NonceName nonceName) throws NonceException {
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
