package net.toknsmith.login.translator;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.tokensmith.jwt.entity.jwk.RSAPublicKey;
import net.tokensmith.jwt.entity.jwk.Use;
import net.toknsmith.login.exception.TranslateException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class JwtRSAPublicKeyTranslator {
    private static String TRANSLATE_MSG = "Failed to communicate with Identity Server";
    private ObjectMapper objectMapper;

    public JwtRSAPublicKeyTranslator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public RSAPublicKey toSingle(InputStream from) throws TranslateException {
        net.toknsmith.login.endpoint.entity.response.api.key.RSAPublicKey key;
        try {
            key = objectMapper.readValue(from, net.toknsmith.login.endpoint.entity.response.api.key.RSAPublicKey.class);
        } catch (IOException e) {
            throw new TranslateException(TRANSLATE_MSG, e);
        }

        return new RSAPublicKey(
                Optional.of(key.getKeyId().toString()),
                Use.SIGNATURE,
                key.getN(),
                key.getE()
        );
    }
}
