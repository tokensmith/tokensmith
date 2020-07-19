package net.tokensmith.authorization.persistence.repository;


import net.tokensmith.authorization.persistence.mapper.RSAPrivateKeyMapper;
import net.tokensmith.jwt.config.JwtAppFactory;
import net.tokensmith.jwt.entity.jwk.SymmetricKey;
import net.tokensmith.jwt.jwe.serialization.JweDeserializer;
import net.tokensmith.repository.entity.RSAPrivateKey;
import net.tokensmith.repository.entity.RSAPrivateKeyBytes;
import net.tokensmith.repository.exceptions.RecordNotFoundException;
import net.tokensmith.repository.repo.CipherRepository;
import net.tokensmith.repository.repo.RsaPrivateKeyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by tommackenzie on 1/28/16.
 */
@Component
public class RsaPrivateKeyRepo implements RsaPrivateKeyRepository, CipherRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(RsaPrivateKeyRepo.class);

    private RSAPrivateKeyMapper rsaPrivateKeyMapper;
    private JwtAppFactory jwtAppFactory;
    private SymmetricKey dbKey;

    @Autowired
    public RsaPrivateKeyRepo(RSAPrivateKeyMapper rsaPrivateKeyMapper, JwtAppFactory jwtAppFactory, SymmetricKey dbKey) {
        this.rsaPrivateKeyMapper = rsaPrivateKeyMapper;
        this.jwtAppFactory = jwtAppFactory;
        this.dbKey = dbKey;
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

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public JweDeserializer getDeserializer() {
        return jwtAppFactory.jweDirectDesializer();
    }

    @Override
    public SymmetricKey getKey() {
        return this.dbKey;
    }

    /**
     * Encrypts a RSAPrivateKey and returns a RSAPrivateKeyBytes
     *
     * @param from an instance of RSAPrivateKey
     * @return an instance of RSAPrivateKeyBytes that is encrypted
     */
    @Override
    public RSAPrivateKeyBytes encrypt(RSAPrivateKey from) {
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
}
