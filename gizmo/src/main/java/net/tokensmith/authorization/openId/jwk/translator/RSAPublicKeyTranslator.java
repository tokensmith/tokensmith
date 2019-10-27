package net.tokensmith.authorization.openId.jwk.translator;

import net.tokensmith.authorization.openId.jwk.entity.RSAPublicKey;
import net.tokensmith.authorization.persistence.entity.RSAPrivateKey;

import org.springframework.stereotype.Component;


/**
 * Created by tommackenzie on 1/1/17.
 */
@Component
public class RSAPublicKeyTranslator {

    public RSAPublicKey to(RSAPrivateKey rsaPrivateKey) {
        RSAPublicKey rsaPublicKey = new RSAPublicKey(
                rsaPrivateKey.getId(),
                rsaPrivateKey.getUse(),
                rsaPrivateKey.getModulus(),
                rsaPrivateKey.getPublicExponent()
        );
        return rsaPublicKey;
    }

}
