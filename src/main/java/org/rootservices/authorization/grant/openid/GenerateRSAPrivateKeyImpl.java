package org.rootservices.authorization.grant.openid;

import org.rootservices.authorization.persistence.entity.PrivateKeyUse;
import org.rootservices.authorization.persistence.entity.RSAPrivateKey;
import org.rootservices.authorization.security.RSAPrivateKeyFactory;
import org.rootservices.jwt.entity.jwk.KeyType;
import org.rootservices.jwt.entity.jwk.Use;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by tommackenzie on 2/15/16.
 */
@Component
public class GenerateRSAPrivateKeyImpl implements GenerateRSAPrivateKey {
    private RSAPrivateKeyFactory rsaPrivateKeyFactory;

    @Autowired
    public GenerateRSAPrivateKeyImpl(RSAPrivateKeyFactory rsaPrivateKeyFactory) {
        this.rsaPrivateKeyFactory = rsaPrivateKeyFactory;
    }

    @Override
    public RSAPrivateKey generate(int keySize) {
        PrivateKey privateKey = rsaPrivateKeyFactory.makePrivateKey(keySize);
        RSAPrivateCrtKey rsaPrivateCrtKey = rsaPrivateKeyFactory.makeRSAPrivateCrtKey(privateKey);
        RSAPrivateKey rsaPrivateKey = translate(rsaPrivateCrtKey);

        return rsaPrivateKey;
    }

    private RSAPrivateKey translate(RSAPrivateCrtKey rsaPrivateCrtKey) {
        RSAPrivateKey rsaPrivateKey = new RSAPrivateKey();

        rsaPrivateKey.setUuid(UUID.randomUUID());
        rsaPrivateKey.setUse(PrivateKeyUse.SIGNATURE);
        rsaPrivateKey.setModulus(rsaPrivateCrtKey.getModulus());
        rsaPrivateKey.setPublicExponent(rsaPrivateCrtKey.getPublicExponent());
        rsaPrivateKey.setPrivateExponent(rsaPrivateCrtKey.getPrivateExponent());
        rsaPrivateKey.setPrimeP(rsaPrivateCrtKey.getPrimeP());
        rsaPrivateKey.setPrimeQ(rsaPrivateCrtKey.getPrimeQ());
        rsaPrivateKey.setPrimeExponentP(rsaPrivateCrtKey.getPrimeExponentP());
        rsaPrivateKey.setPrimeExponentQ(rsaPrivateCrtKey.getPrimeExponentQ());
        rsaPrivateKey.setCrtCoefficient(rsaPrivateCrtKey.getCrtCoefficient());

        return rsaPrivateKey;
    }
}
