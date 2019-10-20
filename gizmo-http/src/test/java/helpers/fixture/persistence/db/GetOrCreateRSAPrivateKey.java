package helpers.fixture.persistence.db;

import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.security.GenerateRSAPrivateKey;
import org.rootservices.authorization.persistence.entity.RSAPrivateKey;
import org.rootservices.authorization.persistence.repository.RsaPrivateKeyRepository;

/**
 * Created by tommackenzie on 2/21/16.
 */

public class GetOrCreateRSAPrivateKey {
    private GenerateRSAPrivateKey generateRSAPrivateKey;
    private RsaPrivateKeyRepository rsaPrivateKeyRepository;

    public GetOrCreateRSAPrivateKey(GenerateRSAPrivateKey generateRSAPrivateKey, RsaPrivateKeyRepository rsaPrivateKeyRepository) {
        this.generateRSAPrivateKey = generateRSAPrivateKey;
        this.rsaPrivateKeyRepository = rsaPrivateKeyRepository;
    }

    public RSAPrivateKey run(int keySize) {
        RSAPrivateKey key;
        try {
            key = rsaPrivateKeyRepository.getMostRecentAndActiveForSigning();
        } catch (RecordNotFoundException e) {
            key = generateRSAPrivateKey.generate(keySize);
            key.setActive(true);
            rsaPrivateKeyRepository.insert(key);
        }

        return key;
    }
}
