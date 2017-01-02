package org.rootservices.authorization.openId.jwk.translator;

import org.rootservices.authorization.persistence.entity.RSAPrivateKey;
import org.rootservices.jwt.entity.jwk.KeyType;
import org.rootservices.jwt.entity.jwk.RSAPublicKey;
import org.rootservices.jwt.entity.jwk.Use;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Created by tommackenzie on 1/1/17.
 */
@Component
public class RSAPublicKeyTranslator {

    public RSAPublicKey to(RSAPrivateKey rsaPrivateKey) {
        RSAPublicKey rsaPublicKey = new RSAPublicKey(
                Optional.of(rsaPrivateKey.getId().toString()),
                KeyType.RSA,
                Use.SIGNATURE,
                rsaPrivateKey.getModulus(),
                rsaPrivateKey.getPublicExponent()
        );
        return rsaPublicKey;
    }

}
