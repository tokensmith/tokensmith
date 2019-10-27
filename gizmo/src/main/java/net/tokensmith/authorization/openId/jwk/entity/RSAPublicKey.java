package net.tokensmith.authorization.openId.jwk.entity;

import net.tokensmith.authorization.persistence.entity.KeyUse;
import net.tokensmith.otter.translatable.Translatable;

import java.math.BigInteger;
import java.util.UUID;

/**
 * Created by tommackenzie on 2/25/17.
 */
public class RSAPublicKey implements Translatable {
    private UUID keyId;
    private KeyUse use;
    private BigInteger n; // modulus
    private BigInteger e; // public exponent

    public RSAPublicKey() {
    }

    public RSAPublicKey(UUID keyId, KeyUse use, BigInteger n, BigInteger e) {
        this.keyId = keyId;
        this.use = use;
        this.n = n;
        this.e = e;
    }

    public UUID getKeyId() {
        return keyId;
    }

    public void setKeyId(UUID keyId) {
        this.keyId = keyId;
    }

    public KeyUse getUse() {
        return use;
    }

    public void setUse(KeyUse use) {
        this.use = use;
    }

    public BigInteger getN() {
        return n;
    }

    public void setN(BigInteger n) {
        this.n = n;
    }

    public BigInteger getE() {
        return e;
    }

    public void setE(BigInteger e) {
        this.e = e;
    }
}
