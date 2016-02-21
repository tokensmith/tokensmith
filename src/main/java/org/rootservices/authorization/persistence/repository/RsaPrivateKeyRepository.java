package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.RSAPrivateKey;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;

/**
 * Created by tommackenzie on 1/28/16.
 */
public interface RsaPrivateKeyRepository {
    void insert(RSAPrivateKey rsaPrivateKey);
    RSAPrivateKey getMostRecentAndActiveForSigning() throws RecordNotFoundException;
}
