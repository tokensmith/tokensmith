package net.tokensmith.repository.repo;


import net.tokensmith.repository.entity.RSAPrivateKey;
import net.tokensmith.repository.exceptions.RecordNotFoundException;

import java.util.List;
import java.util.UUID;

/**
 * Created by tommackenzie on 1/28/16.
 */
public interface RsaPrivateKeyRepository {
    void insert(RSAPrivateKey rsaPrivateKey);
    RSAPrivateKey getMostRecentAndActiveForSigning() throws RecordNotFoundException;
    List<RSAPrivateKey> getWhereActiveAndUseIsSign(Integer limit, Integer offset);
    RSAPrivateKey getByIdActiveSign(UUID id) throws RecordNotFoundException;
}
