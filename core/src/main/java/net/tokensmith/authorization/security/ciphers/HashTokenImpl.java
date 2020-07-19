package net.tokensmith.authorization.security.ciphers;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class HashTokenImpl implements HashToken {
    private static final Logger LOGGER = LoggerFactory.getLogger(HashTokenImpl.class);
    private static String ALG_SHA3_512 = "SHA3-512";

    @Override
    public String run(String plainText) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance(ALG_SHA3_512);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(e.getMessage(), e);
        }

        byte[] hashed = digest.digest(plainText.getBytes(StandardCharsets.UTF_8));

        return Hex.encodeHexString(hashed);
    }
}
