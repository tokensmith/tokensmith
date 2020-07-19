package net.tokensmith.authorization.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateCrtKeySpec;

/**
 * Created by tommackenzie on 2/13/16.
 */
@Component
public class RSAPrivateKeyFactory {
    private KeyPairGenerator keyPairGenerator;
    private KeyFactory keyFactory;

    @Autowired
    public RSAPrivateKeyFactory(KeyPairGenerator keyPairGenerator, KeyFactory keyFactory) {
        this.keyPairGenerator = keyPairGenerator;
        this.keyFactory = keyFactory;
    }

    public synchronized PrivateKey makePrivateKey(int keySize) {

        // not thread safe so its synchronized ^
        keyPairGenerator.initialize(keySize);
        KeyPair keyPair = keyPairGenerator.genKeyPair();

        return keyPair.getPrivate();
    }

    public RSAPrivateCrtKey makeRSAPrivateCrtKey(PrivateKey privateKey) {


        RSAPrivateCrtKeySpec rsaPrivateCrtKeySpec = null;
        try {
            rsaPrivateCrtKeySpec = keyFactory.getKeySpec(privateKey, RSAPrivateCrtKeySpec.class);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        RSAPrivateCrtKey rsaPrivateCrtKey = null;
        try {
            rsaPrivateCrtKey = (RSAPrivateCrtKey) keyFactory.generatePrivate(rsaPrivateCrtKeySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return rsaPrivateCrtKey;
    }
}
