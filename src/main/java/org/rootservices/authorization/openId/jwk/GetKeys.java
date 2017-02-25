package org.rootservices.authorization.openId.jwk;

import org.rootservices.authorization.openId.jwk.entity.RSAPublicKey;
import org.rootservices.authorization.openId.jwk.exception.NotFoundException;
import org.rootservices.authorization.openId.jwk.translator.RSAPublicKeyTranslator;
import org.rootservices.authorization.persistence.entity.RSAPrivateKey;
import org.rootservices.authorization.persistence.exceptions.RecordNotFoundException;
import org.rootservices.authorization.persistence.repository.RsaPrivateKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by tommackenzie on 1/4/17.
 */
@Component
public class GetKeys {
    private RSAPublicKeyTranslator rsaPublicKeyTranslator;
    private RsaPrivateKeyRepository rsaPrivateKeyRepository;

    private static String KEY_NOT_FOUND = "key was not found";
    private static Integer LIMIT = 20;

    @Autowired
    public GetKeys(RSAPublicKeyTranslator rsaPublicKeyTranslator, RsaPrivateKeyRepository rsaPrivateKeyRepository) {
        this.rsaPublicKeyTranslator = rsaPublicKeyTranslator;
        this.rsaPrivateKeyRepository = rsaPrivateKeyRepository;
    }

    public RSAPublicKey getPublicKeyById(UUID id) throws NotFoundException {
        RSAPrivateKey rsaPrivateKey = null;
        try {
            rsaPrivateKey = rsaPrivateKeyRepository.getByIdActiveSign(id);
        } catch (RecordNotFoundException e) {
            throw new NotFoundException(KEY_NOT_FOUND, e);
        }
        return rsaPublicKeyTranslator.to(rsaPrivateKey);
    }

    public List<RSAPublicKey> getPublicKeys(Integer page) {
        Integer offset = calculateOffset(page);
        List<RSAPrivateKey> rsaPrivateKeys = rsaPrivateKeyRepository.getWhereActiveAndUseIsSign(LIMIT, offset);

        List<RSAPublicKey> rsaPublicKeys = new ArrayList<>();
        for(RSAPrivateKey rsaPrivateKey: rsaPrivateKeys) {
            rsaPublicKeys.add(rsaPublicKeyTranslator.to(rsaPrivateKey));
        }

        return rsaPublicKeys;
    }

    protected Integer calculateOffset(Integer page) {
        Integer offset = 0;
        if (page > 1) {
            offset = (page - 1) * LIMIT;
        }

        return offset;
    }
}
