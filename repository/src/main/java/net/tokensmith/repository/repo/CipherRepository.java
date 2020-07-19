package net.tokensmith.repository.repo;

import net.tokensmith.jwt.builder.compact.EncryptedCompactBuilder;
import net.tokensmith.jwt.builder.exception.CompactException;
import net.tokensmith.jwt.entity.jwe.EncryptionAlgorithm;
import net.tokensmith.jwt.entity.jwk.SymmetricKey;
import net.tokensmith.jwt.entity.jwt.header.Algorithm;
import net.tokensmith.jwt.jwe.entity.JWE;
import net.tokensmith.jwt.jwe.factory.exception.CipherException;
import net.tokensmith.jwt.jwe.serialization.JweDeserializer;
import net.tokensmith.jwt.jwe.serialization.exception.KeyException;
import net.tokensmith.jwt.serialization.exception.DecryptException;
import net.tokensmith.jwt.serialization.exception.JsonToJwtException;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;


public interface CipherRepository {

    Logger getLogger();
    SymmetricKey getKey();
    JweDeserializer getDeserializer();

    default byte[] encrypt(byte[] payload) {
        EncryptedCompactBuilder compactBuilder = new EncryptedCompactBuilder();

        SymmetricKey key = getKey();

        ByteArrayOutputStream jwe = null;
        try {
            jwe = compactBuilder.encAlg(EncryptionAlgorithm.AES_GCM_256)
                    .alg(Algorithm.DIRECT)
                    .encAlg(EncryptionAlgorithm.AES_GCM_256)
                    .payload(payload)
                    .cek(key)
                    .build();
        } catch (CompactException e) {
            getLogger().error(e.getMessage(), e);
        }

        return jwe.toByteArray();
    }

    default byte[] decrypt(byte[] cipherText) {
        SymmetricKey key = getKey();
        JweDeserializer jweDeserializer = getDeserializer();

        JWE to = null;
        try {
            String cipher = new String(cipherText);
            to = jweDeserializer.stringToJWE(cipher, key);
        } catch (JsonToJwtException e) {
            getLogger().error(e.getMessage(), e);
        } catch (DecryptException e) {
            getLogger().error(e.getMessage(), e);
        } catch (CipherException e) {
            getLogger().error(e.getMessage(), e);
        } catch (KeyException e) {
            getLogger().error(e.getMessage(), e);
        }

        return to.getPayload();
    }
}
