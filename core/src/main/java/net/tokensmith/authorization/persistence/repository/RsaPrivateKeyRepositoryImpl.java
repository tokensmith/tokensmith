package net.tokensmith.authorization.persistence.repository;

import net.tokensmith.jwt.builder.compact.EncryptedCompactBuilder;
import net.tokensmith.jwt.builder.exception.CompactException;
import net.tokensmith.jwt.config.JwtAppFactory;
import net.tokensmith.jwt.entity.jwe.EncryptionAlgorithm;
import net.tokensmith.jwt.entity.jwk.SymmetricKey;
import net.tokensmith.jwt.entity.jwk.Use;
import net.tokensmith.jwt.entity.jwt.header.Algorithm;
import net.tokensmith.jwt.jwe.entity.JWE;
import net.tokensmith.jwt.jwe.factory.exception.CipherException;
import net.tokensmith.jwt.jwe.serialization.JweDeserializer;
import net.tokensmith.jwt.jwe.serialization.exception.KeyException;
import net.tokensmith.jwt.serialization.exception.DecryptException;
import net.tokensmith.jwt.serialization.exception.JsonToJwtException;
import net.tokensmith.repository.entity.RSAPrivateKey;
import net.tokensmith.authorization.persistence.mapper.RSAPrivateKeyMapper;
import net.tokensmith.repository.entity.RSAPrivateKeyBytes;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.repository.repo.RsaPrivateKeyRepository;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by tommackenzie on 1/28/16.
 */
@Component
public class RsaPrivateKeyRepositoryImpl implements RsaPrivateKeyRepository {
    private static final Logger LOGGER = LogManager.getLogger(RsaPrivateKeyRepositoryImpl.class);

    private RSAPrivateKeyMapper rsaPrivateKeyMapper;
    private JwtAppFactory jwtAppFactory;
    private static String KEY_ID = "XXX";
    private static String SECRET = "LjF8D5qi24-dJQRFeAshXmJLhtQzn62iLt8f5ftDR_Q";

    @Autowired
    public RsaPrivateKeyRepositoryImpl(RSAPrivateKeyMapper rsaPrivateKeyMapper, JwtAppFactory jwtAppFactory) {
        this.rsaPrivateKeyMapper = rsaPrivateKeyMapper;
        this.jwtAppFactory = jwtAppFactory;
    }

    @Override
    public void insert(RSAPrivateKey rsaPrivateKey) {
        RSAPrivateKeyBytes encryptedKey = encrypt(rsaPrivateKey);
        rsaPrivateKeyMapper.insert(encryptedKey);
    }

    @Override
    public RSAPrivateKey getMostRecentAndActiveForSigning() throws RecordNotFoundException {
        RSAPrivateKeyBytes encryptedKey = rsaPrivateKeyMapper.getMostRecentAndActiveForSigning();

        if (encryptedKey == null) {
            throw new RecordNotFoundException("Couldn't find RSAPrivateKey");
        }
        RSAPrivateKey key = decrypt(encryptedKey);
        return key;
    }

    @Override
    public List<RSAPrivateKey> getWhereActiveAndUseIsSign(Integer limit, Integer offset) {
        List<RSAPrivateKeyBytes> encryptedKeys = rsaPrivateKeyMapper.getWhereActiveAndUseIsSign(limit, offset);
        List<RSAPrivateKey> keys = encryptedKeys.stream().map(this::decrypt).collect(Collectors.toList());
        return keys;
    }

    @Override
    public RSAPrivateKey getByIdActiveSign(UUID id) throws RecordNotFoundException {
        RSAPrivateKeyBytes encryptedKey = rsaPrivateKeyMapper.getByIdActiveSign(id);
        if (encryptedKey == null) {
            throw new RecordNotFoundException("RSAPrivateKey was not found");
        }
        RSAPrivateKey key = decrypt(encryptedKey);
        return key;
    }

    /**
     * Encrypts a RSAPrivateKey and returns a RSAPrivateKeyBytes
     *
     * @param from an instance of RSAPrivateKey
     * @return an instance of RSAPrivateKeyBytes that is encrypted
     */
    protected RSAPrivateKeyBytes encrypt(RSAPrivateKey from) {
        RSAPrivateKeyBytes to = from(from);

        to.setModulus(encrypt(to.getModulus()));
        to.setPublicExponent(encrypt(to.getPublicExponent()));
        to.setPrivateExponent(encrypt(to.getPrivateExponent()));
        to.setPrimeP(encrypt(to.getPrimeP()));
        to.setPrimeQ(encrypt(to.getPrimeQ()));
        to.setPrimeExponentP(encrypt(to.getPrimeExponentP()));
        to.setPrimeExponentQ(encrypt(to.getPrimeExponentQ()));
        to.setCrtCoefficient(encrypt(to.getCrtCoefficient()));

        return to;
    }

    /**
     * Decrypts a RSAPrivateKeyBytes and returns a RSAPrivateKey
     *
     * @param from an instance of RSAPrivateKeyBytes that is encrypted.
     * @return an instance of RSAPrivateKey that is decrypted
     */
    protected RSAPrivateKey decrypt(RSAPrivateKeyBytes from) {

        // decrypt from.
        from.setModulus(decrypt(from.getModulus()));
        from.setPublicExponent(decrypt(from.getPublicExponent()));
        from.setPrivateExponent(decrypt(from.getPrivateExponent()));
        from.setPrimeP(decrypt(from.getPrimeP()));
        from.setPrimeQ(decrypt(from.getPrimeQ()));
        from.setPrimeExponentP(decrypt(from.getPrimeExponentP()));
        from.setPrimeExponentQ(decrypt(from.getPrimeExponentQ()));
        from.setCrtCoefficient(decrypt(from.getCrtCoefficient()));

        // translate to RSAPrivateKey
        RSAPrivateKey to = to(from);
        return to;
    }

    /**
     * Translates a RSAPrivateKeyBytes to RSAPrivateKey.
     * Assumes from is decrypted.
     *
     * @param from the RSAPrivateKeyBytes to translate
     * @return an instance of RSAPrivateKey
     */
    protected RSAPrivateKey to(RSAPrivateKeyBytes from) {
        RSAPrivateKey to = new RSAPrivateKey();

        to.setId(from.getId());
        to.setUse(from.getUse());

        BigInteger toBigInteger = null;

        toBigInteger = toBigInteger(from.getModulus());
        to.setModulus(toBigInteger);

        toBigInteger = toBigInteger(from.getPublicExponent());
        to.setPublicExponent(toBigInteger);

        toBigInteger = toBigInteger(from.getPrivateExponent());
        to.setPrivateExponent(toBigInteger);

        toBigInteger = toBigInteger(from.getPrimeP());
        to.setPrimeP(toBigInteger);

        toBigInteger = toBigInteger(from.getPrimeQ());
        to.setPrimeQ(toBigInteger);

        toBigInteger = toBigInteger(from.getPrimeExponentP());
        to.setPrimeExponentP(toBigInteger);

        toBigInteger = toBigInteger(from.getPrimeExponentQ());
        to.setPrimeExponentQ(toBigInteger);

        toBigInteger = toBigInteger(from.getCrtCoefficient());
        to.setCrtCoefficient(toBigInteger);

        to.setActive(from.isActive());
        to.setCreatedAt(from.getCreatedAt());
        to.setUpdatedAt(from.getUpdatedAt());
        return to;
    }

    /**
     * Translates an instance of RSAPrivateKey to a RSAPrivateKeyBytes
     * both are expected to be plain text.
     *
     * @param from an instance of RSAPrivateKey
     * @return an instance of RSAPrivateKeyBytes
     */
    protected RSAPrivateKeyBytes from(RSAPrivateKey from) {
        RSAPrivateKeyBytes to = new RSAPrivateKeyBytes();

        to.setId(from.getId());
        to.setUse(from.getUse());

        byte[] toBytes = null;
        toBytes = toBytes(from.getModulus());

        to.setModulus(toBytes);

        toBytes = toBytes(from.getPublicExponent());
        to.setPublicExponent(toBytes);

        toBytes = toBytes(from.getPrivateExponent());
        to.setPrivateExponent(toBytes);

        toBytes = toBytes(from.getPrimeP());
        to.setPrimeP(toBytes);

        toBytes = toBytes(from.getPrimeQ());
        to.setPrimeQ(toBytes);

        toBytes = toBytes(from.getPrimeExponentP());
        to.setPrimeExponentP(toBytes);

        toBytes = toBytes(from.getPrimeExponentQ());
        to.setPrimeExponentQ(toBytes);

        toBytes = toBytes(from.getCrtCoefficient());
        to.setCrtCoefficient(toBytes);

        to.setActive(from.isActive());
        to.setCreatedAt(from.getCreatedAt());
        to.setUpdatedAt(from.getUpdatedAt());

        return to;
    }

    protected BigInteger toBigInteger(byte[] from) {
        return new BigInteger(from);
    }

    protected byte[] toBytes(BigInteger from) {
        return from.toByteArray();
    }

    protected byte[] encrypt(byte[] payload) {
        EncryptedCompactBuilder compactBuilder = new EncryptedCompactBuilder();

        SymmetricKey key = new SymmetricKey(
                Optional.of(KEY_ID), SECRET, Use.ENCRYPTION
        );

        ByteArrayOutputStream jwe = null;
        try {
            jwe = compactBuilder.encAlg(EncryptionAlgorithm.AES_GCM_256)
                    .alg(Algorithm.DIRECT)
                    .encAlg(EncryptionAlgorithm.AES_GCM_256)
                    .payload(payload)
                    .cek(key)
                    .build();
        } catch (CompactException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return jwe.toByteArray();
    }

    protected byte[] decrypt(byte[] cipherText) {
        SymmetricKey key = new SymmetricKey(
                Optional.of(KEY_ID), SECRET, Use.ENCRYPTION
        );

        JweDeserializer jweDeserializer = jwtAppFactory.jweDirectDesializer();

        JWE to = null;
        try {
            String cipher = new String(cipherText);
            to = jweDeserializer.stringToJWE(cipher, key);
        } catch (JsonToJwtException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (DecryptException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (CipherException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (KeyException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return to.getPayload();
    }
}
