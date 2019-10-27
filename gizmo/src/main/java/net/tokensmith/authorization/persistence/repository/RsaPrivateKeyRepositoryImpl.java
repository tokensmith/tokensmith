package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.authorization.persistence.entity.RSAPrivateKey;
import net.tokensmith.authorization.persistence.exceptions.RecordNotFoundException;
import net.tokensmith.authorization.persistence.mapper.RSAPrivateKeyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

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

    @Override
    public List<RSAPrivateKey> getWhereActiveAndUseIsSign(Integer limit, Integer offset) {
        return rsaPrivateKeyMapper.getWhereActiveAndUseIsSign(limit, offset);
    }

    @Override
    public RSAPrivateKey getByIdActiveSign(UUID id) throws RecordNotFoundException {
        RSAPrivateKey rsaPrivateKey = rsaPrivateKeyMapper.getByIdActiveSign(id);
        if (rsaPrivateKey == null) {
            throw new RecordNotFoundException("RSAPrivateKey was not found");
        }
        return rsaPrivateKey;
    }
}
