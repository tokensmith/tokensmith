package org.rootservices.authorization.security;

import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateCrtKey;

/**
 * Created by tommackenzie on 2/13/16.
 */
public interface RSAPrivateKeyFactory {
    PrivateKey makePrivateKey(int keySize);
    RSAPrivateCrtKey makeRSAPrivateCrtKey(PrivateKey privateKey);
}
