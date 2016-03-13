package org.rootservices.authorization.persistence.repository;

import org.rootservices.authorization.persistence.entity.RSAPrivateKey;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.mapper.RSAPrivateKeyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by tommackenzie on 1/28/16.
 */
@Component
public class RsaPrivateKeyRepositoryImpl implements RsaPrivateKeyRepository {

    private RSAPrivateKeyMapper rsaPrivateKeyMapper;

    @Autowired
    public RsaPrivateKeyRepositoryImpl(RSAPrivateKeyMapper rsaPrivateKeyMapper) {
        this.rsaPrivateKeyMapper = rsaPrivateKeyMapper;
    }

    @Override
    public void insert(RSAPrivateKey rsaPrivateKey) {
        rsaPrivateKeyMapper.insert(rsaPrivateKey);
    }

    @Override
    public RSAPrivateKey getMostRecentAndActiveForSigning() throws RecordNotFoundException {
        RSAPrivateKey rsaPrivateKey = rsaPrivateKeyMapper.getMostRecentAndActiveForSigning();

        if (rsaPrivateKey == null) {
            throw new RecordNotFoundException("Couldn't find RSAPrivateKey");
        }
        return rsaPrivateKey;
    }
}