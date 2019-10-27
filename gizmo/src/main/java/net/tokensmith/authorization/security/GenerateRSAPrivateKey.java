package net.tokensmith.authorization.security;

import net.tokensmith.authorization.persistence.entity.KeyUse;
import net.tokensmith.authorization.persistence.entity.RSAPrivateKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.util.UUID;

/**
 * Created by tommackenzie on 2/15/16.
 */
@Component
public class GenerateRSAPrivateKey {
    private RSAPrivateKeyFactory rsaPrivateKeyFactory;

    @Autowired
    public GenerateRSAPrivateKey(RSAPrivateKeyFactory rsaPrivateKeyFactory) {
        this.rsaPrivateKeyFactory = rsaPrivateKeyFactory;
    }

    public RSAPrivateKey generate(int keySize) {
        PrivateKey privateKey = rsaPrivateKeyFactory.makePrivateKey(keySize);
        RSAPrivateCrtKey rsaPrivateCrtKey = rsaPrivateKeyFactory.makeRSAPrivateCrtKey(privateKey);
        RSAPrivateKey rsaPrivateKey = translate(rsaPrivateCrtKey);

        return rsaPrivateKey;
    }

    private RSAPrivateKey translate(RSAPrivateCrtKey rsaPrivateCrtKey) {
        RSAPrivateKey rsaPrivateKey = new RSAPrivateKey();

        rsaPrivateKey.setId(UUID.randomUUID());
        rsaPrivateKey.setUse(KeyUse.SIGNATURE);
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
