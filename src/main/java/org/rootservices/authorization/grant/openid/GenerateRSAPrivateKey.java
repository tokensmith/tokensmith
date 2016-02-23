package org.rootservices.authorization.grant.openid;

import org.rootservices.authorization.persistence.entity.RSAPrivateKey;

/**
 * Created by tommackenzie on 2/15/16.
 */
public interface GenerateRSAPrivateKey {
    RSAPrivateKey generate(int keySize);
}
