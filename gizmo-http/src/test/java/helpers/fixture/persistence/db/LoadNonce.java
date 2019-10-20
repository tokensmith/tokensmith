package helpers.fixture.persistence.db;

import org.rootservices.authorization.nonce.entity.NonceName;
import org.rootservices.authorization.persistence.entity.Nonce;
import org.rootservices.authorization.persistence.entity.NonceType;
import org.rootservices.authorization.persistence.entity.ResourceOwner;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.NonceRepository;
import org.rootservices.authorization.persistence.repository.NonceTypeRepository;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class LoadNonce {
    private NonceTypeRepository nonceTypeRepository;
    private NonceRepository nonceRepository;

    public LoadNonce(NonceTypeRepository nonceTypeRepository, NonceRepository nonceRepository) {
        this.nonceTypeRepository = nonceTypeRepository;
        this.nonceRepository = nonceRepository;
    }

    public Nonce welcome(ResourceOwner ro, byte[] nonceValue) {
        return insertNonce(ro, nonceValue, NonceName.WELCOME);

    }

    public Nonce resetPassword(ResourceOwner ro, byte[] nonceValue) {
        return insertNonce(ro, nonceValue, NonceName.RESET_PASSWORD);
    }

    protected Nonce insertNonce(ResourceOwner ro, byte[] nonceValue, NonceName nonceName) {
        NonceType nonceType;

        try {
            nonceType = nonceTypeRepository.getByName(nonceName);
        } catch (RecordNotFoundException e) {
            nonceType = insertNonceType(nonceName);
        }

        Nonce nonce = insertNonce(nonceType, ro, nonceValue);

        return nonce;
    }

    protected NonceType insertNonceType(NonceName nonceName) {
        NonceType nonceType = new NonceType();
        nonceType.setId(UUID.randomUUID());
        nonceType.setName(nonceName.toString());

        nonceTypeRepository.insert(nonceType);

        return nonceType;
    }

    protected Nonce insertNonce(NonceType nonceType, ResourceOwner ro, byte[] nonceValue) {
        Nonce nonce = new Nonce();

        nonce.setId(UUID.randomUUID());
        nonce.setNonceType(nonceType);
        nonce.setResourceOwner(ro);
        nonce.setNonce(nonceValue);
        nonce.setExpiresAt(OffsetDateTime.now().plusSeconds(nonceType.getSecondsToExpiry()));

        nonceRepository.insert(nonce);

        return nonce;
    }
}
