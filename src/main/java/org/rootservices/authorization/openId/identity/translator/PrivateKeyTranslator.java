package org.rootservices.authorization.openId.identity.translator;

import org.rootservices.authorization.persistence.entity.RSAPrivateKey;
import org.rootservices.jwt.entity.jwk.RSAKeyPair;

/**
 * Created by tommackenzie on 2/1/16.
 */
public interface PrivateKeyTranslator {
    RSAPrivateKey to(RSAKeyPair rsaKeyPair);
    RSAKeyPair from(RSAPrivateKey privateKey);
}
