package org.rootservices.authorization.openId.identity.translator;

import org.rootservices.authorization.persistence.entity.RSAPrivateKey;
import org.rootservices.authorization.persistence.entity.PrivateKeyUse;
import org.rootservices.jwt.entity.jwk.KeyType;
import org.rootservices.jwt.entity.jwk.RSAKeyPair;
import org.rootservices.jwt.entity.jwk.Use;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Created by tommackenzie on 2/1/16.
 */
@Component
public class PrivateKeyTranslator {


    public RSAPrivateKey to(RSAKeyPair rsaKeyPair) {
        RSAPrivateKey privateKey = new RSAPrivateKey();

        privateKey.setUse(PrivateKeyUse.SIGNATURE);
        privateKey.setModulus(rsaKeyPair.getN());
        privateKey.setPublicExponent(rsaKeyPair.getE());
        privateKey.setPrivateExponent(rsaKeyPair.getD());
        privateKey.setPrimeP(rsaKeyPair.getP());
        privateKey.setPrimeQ(rsaKeyPair.getQ());
        privateKey.setPrimeExponentP(rsaKeyPair.getDp());
        privateKey.setPrimeExponentQ(rsaKeyPair.getDq());
        privateKey.setCrtCoefficient(rsaKeyPair.getQi());

        return privateKey;
    }

    public RSAKeyPair from(RSAPrivateKey privateKey) {

        RSAKeyPair keyPair = new RSAKeyPair(
                Optional.of(privateKey.getUuid().toString()),
                KeyType.RSA,
                Use.SIGNATURE,
                privateKey.getModulus(),
                privateKey.getPublicExponent(),
                privateKey.getPrivateExponent(),
                privateKey.getPrimeP(),
                privateKey.getPrimeQ(),
                privateKey.getPrimeExponentP(),
                privateKey.getPrimeExponentQ(),
                privateKey.getCrtCoefficient()
        );

        return keyPair;
    }
}
